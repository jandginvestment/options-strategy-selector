"""
NiftyTrader — Railway FastAPI Server
Fetches live NSE market data (Nifty 50 + India VIX) via yfinance.
Deployed on Railway, called by the Kotlin Android app.

Endpoints:
  GET /          → API info
  GET /health    → health check
  GET /market    → full market data payload
  GET /history   → last 2h of VIX readings
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime, timezone, timedelta
from collections import deque
import yfinance as yf
import asyncio
import logging

logging.basicConfig(level=logging.INFO)
log = logging.getLogger("niftytrader")

app = FastAPI(title="NiftyTrader API", version="1.0.0")

# Allow all origins (Kotlin app, browser, tablet)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["GET"],
    allow_headers=["*"],
)

# ─── Constants ────────────────────────────────────────────────
IST = timezone(timedelta(hours=5, minutes=30))
DXB = timezone(timedelta(hours=4))

# NSE weekly expiry Thursdays 2026
EXPIRY_DATES = [
    "2026-06-19", "2026-06-26",
    "2026-07-02", "2026-07-09", "2026-07-16", "2026-07-23", "2026-07-30",
    "2026-08-06", "2026-08-13", "2026-08-20", "2026-08-27",
    "2026-09-03", "2026-09-10", "2026-09-17", "2026-09-24", "2026-09-29",
    "2026-10-01", "2026-10-08", "2026-10-15", "2026-10-22", "2026-10-29",
    "2026-11-05", "2026-11-12", "2026-11-19", "2026-11-26",
    "2026-12-03", "2026-12-10", "2026-12-17", "2026-12-24", "2026-12-29",
]

# In-memory VIX history (timestamp, value) — last 2 hours
vix_history: deque = deque(maxlen=100)

# Cache last successful fetch to serve if yfinance fails
_cache: dict = {}


# ─── Helpers ──────────────────────────────────────────────────

def get_market_phase() -> str:
    """Returns current NSE market phase based on IST time."""
    now_ist = datetime.now(IST)
    weekday = now_ist.weekday()  # 0=Mon, 6=Sun
    if weekday >= 5:
        return "weekend"
    minutes = now_ist.hour * 60 + now_ist.minute
    if minutes < 9 * 60 + 15:
        return "pre"
    if minutes < 10 * 60 + 30:
        return "wait"
    if minutes < 14 * 60 + 30:
        return "entry"
    if minutes < 15 * 60 + 30:
        return "late"
    return "closed"


def get_best_expiry() -> dict:
    """Picks the best expiry Thursday: 6–14 days out, never today if Thursday."""
    now_ist = datetime.now(IST).date()
    is_thursday = now_ist.weekday() == 3

    for d in EXPIRY_DATES:
        exp = datetime.strptime(d, "%Y-%m-%d").date()
        days = (exp - now_ist).days
        if days <= 0:
            continue
        # Never pick today on expiry day
        if is_thursday and days == 0:
            continue
        # Ideal window: 6–14 days
        if 6 <= days <= 14:
            return {"date": d, "days": days, "label": exp.strftime("%d %b %Y")}
        # Accept next one beyond 14 days if nothing found yet
    # Fallback: first future expiry
    for d in EXPIRY_DATES:
        exp = datetime.strptime(d, "%Y-%m-%d").date()
        days = (exp - now_ist).days
        if days > 0:
            return {"date": d, "days": days, "label": exp.strftime("%d %b %Y")}
    return {"date": "", "days": 0, "label": "Unknown"}


def detect_vix_direction() -> str:
    """Compares latest VIX to reading ~30 min ago."""
    if len(vix_history) < 2:
        return "stable"
    latest = vix_history[-1]["v"]
    cutoff = datetime.now(timezone.utc) - timedelta(minutes=35)
    old_readings = [h for h in vix_history if h["t"] < cutoff]
    if not old_readings:
        return "stable"
    old_vix = old_readings[-1]["v"]
    change_pct = (latest - old_vix) / old_vix * 100
    if change_pct > 5:
        return "rising_sharp"
    if change_pct > 2:
        return "rising"
    if change_pct < -2:
        return "falling"
    return "stable"


def fetch_market_data() -> dict:
    """Fetches Nifty 50 + India VIX from Yahoo Finance via yfinance."""
    log.info("Fetching market data from Yahoo Finance...")

    try:
        # history() is much more stable than fast_info when markets are closed (weekends)
        n_hist = yf.Ticker("^NSEI").history(period="5d")
        v_hist = yf.Ticker("^INDIAVIX").history(period="2d")
        nifty = round(float(n_hist['Close'].iloc[-1]), 2)
        prev_close = round(float(n_hist['Close'].iloc[-2]), 2)
        vix = round(float(v_hist['Close'].iloc[-1]), 2)
        gap_pct = round((nifty - prev_close) / prev_close * 100, 2) if prev_close else 0.0
    except Exception as e:
        log.warning(f"History fetch failed: {e}. Trying fast_info fallback...")
        tickers = yf.Tickers("^NSEI ^INDIAVIX")
        nifty_info = tickers.tickers["^NSEI"].fast_info
        vix_info   = tickers.tickers["^INDIAVIX"].fast_info
        nifty     = round(float(nifty_info.last_price), 2)
        prev_close= round(float(nifty_info.previous_close), 2)
        vix       = round(float(vix_info.last_price), 2)
        gap_pct   = round((nifty - prev_close) / prev_close * 100, 2) if prev_close else 0.0

    log.info(f"Nifty={nifty} VIX={vix} PrevClose={prev_close} Gap={gap_pct}%")
    return {
        "nifty": nifty,
        "vix": vix,
        "prevClose": prev_close,
        "gapPct": gap_pct,
    }


# ─── Background refresh (every 3 min during market hours) ────
async def background_refresh():
    """Keeps data fresh automatically on the server."""
    while True:
        phase = get_market_phase()
        if phase in ("entry", "wait", "pre", "late"):
            try:
                data = fetch_market_data()
                now_utc = datetime.now(timezone.utc)
                vix_history.append({"v": data["vix"], "t": now_utc})
                expiry = get_best_expiry()
                _cache.update({
                    **data,
                    "vixDirection": detect_vix_direction(),
                    "bestExpiry": expiry["date"],
                    "daysToExpiry": expiry["days"],
                    "expiryLabel": expiry["label"],
                    "marketPhase": phase,
                    "timestamp": datetime.now(IST).isoformat(),
                    "cached": False,
                })
                log.info(f"Cache refreshed — phase={phase}")
            except Exception as e:
                log.error(f"Background refresh failed: {e}")
        await asyncio.sleep(180)  # 3 minutes


@app.on_event("startup")
async def startup():
    asyncio.create_task(background_refresh())
    # Initial fetch
    try:
        data = fetch_market_data()
        expiry = get_best_expiry()
        now_utc = datetime.now(timezone.utc)
        vix_history.append({"v": data["vix"], "t": now_utc})
        _cache.update({
            **data,
            "vixDirection": "stable",
            "bestExpiry": expiry["date"],
            "daysToExpiry": expiry["days"],
            "expiryLabel": expiry["label"],
            "marketPhase": get_market_phase(),
            "timestamp": datetime.now(IST).isoformat(),
            "cached": False,
        })
        log.info("Initial fetch complete")
    except Exception as e:
        log.error(f"Initial fetch failed: {e}")


# ─── Endpoints ────────────────────────────────────────────────

@app.get("/")
def root():
    return {
        "name": "NiftyTrader API",
        "version": "1.0.0",
        "endpoints": ["/health", "/market", "/history"],
        "owner": "jandginvestment",
    }


@app.get("/health")
def health():
    return {"status": "ok", "timestamp": datetime.now(IST).isoformat()}


@app.get("/market")
def market():
    """Main endpoint — returns all market data for the Kotlin app."""
    if not _cache:
        # Cache empty (server just started, fetch failed) — try live
        try:
            data = fetch_market_data()
            expiry = get_best_expiry()
            return {
                **data,
                "vixDirection": "stable",
                "bestExpiry": expiry["date"],
                "daysToExpiry": expiry["days"],
                "expiryLabel": expiry["label"],
                "marketPhase": get_market_phase(),
                "timestamp": datetime.now(IST).isoformat(),
                "cached": False,
            }
        except Exception as e:
            return {"error": str(e), "message": "Market data unavailable. NSE may be closed."}

    # Always refresh the live fields
    result = dict(_cache)
    result["marketPhase"] = get_market_phase()
    result["vixDirection"] = detect_vix_direction()
    expiry = get_best_expiry()
    result["bestExpiry"] = expiry["date"]
    result["daysToExpiry"] = expiry["days"]
    result["expiryLabel"] = expiry["label"]
    return result


@app.get("/history")
def history():
    """Returns last 2h of VIX readings for charting / trend analysis."""
    return {
        "readings": [
            {"vix": h["v"], "time": h["t"].isoformat()}
            for h in vix_history
        ],
        "count": len(vix_history),
    }
