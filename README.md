# NiftyTrader — Options Strategy Selector

> **xyz's personal NSE Nifty 50 options strategy selector & trade manager**
> Built for daily pre-market analysis from xyz → IST conversion included.

---

## Features

- 📊 **Setup Tab** — Enter VIX + Nifty, get instant 4-gate pre-trade check
- ⚡ **Strategy Engine** — Auto-selects Iron Condor / Long Strangle / Bull Put Spread based on VIX
- 🏗️ **Strike Calculator** — Auto-computes all 4 legs with estimated prices
- 🤖 **AI Analysis** — Claude API integration for custom questions
- 📈 **Position Tracker** — Track open P&L, distance to strikes, stop alerts
- 📓 **Trade Journal** — Log trades, track win rate, export CSV
- 📚 **References** — In-app strategy guides + daily routine
- 🌐 **Live Data Fetch** — Yahoo Finance / NSE / Claude web search
- 📱 **Native Kotlin App** — Fully native Android application
- 🚀 **Railway API** — Dedicated Python FastAPI backend

---

## Strategy Matrix

| VIX Level | Strategy | Action |
|-----------|----------|--------|
| < 14 | Long Strangle | Buy aggressively (both legs < ₹120) |
| 14–16 | Long Strangle | Buy normally (both legs < ₹150) |
| 16–18 | Iron Condor | Sell premium ✅ Core strategy |
| 18–20 | Bull Put Spread | Caution mode, defined risk |
| > 20 | SKIP + PP | Portfolio protection mode |

---

## Trading Rules (Hard-Coded)

- ❌ Never enter before 9:00 AM xyz (10:30 AM IST)
- ❌ Never buy options when VIX > 17
- ❌ Never pay > ₹150 per strangle leg
- ❌ Never hold positions into weekend
- ❌ Never sell ITM options
- ✅ Always exit at 50% profit
- ✅ Always use 2x credit as stop loss
- ✅ PP: buy when VIX LOW (< 16) — opposite to income trades

---

## Paper Trade History (all-time: +₹3,796)

| Date | Strategy | P&L | Lesson |
|------|----------|-----|--------|
| May 12 | Long Strangle | +₹4,599 | Won despite bad entry time |
| May 13 | Long Strangle | -₹9,435 | Never buy at VIX 19 |
| May 19 | Long Strangle | +₹5,853 | Both legs expired ITM! |
| May 20 | Iron Condor | +₹3,200 | First correct IC entry |

---

## Setup

The system consists of two parts: a Python backend and a Kotlin Android app.

### 1. Railway API (Backend)
1. Deploy the `server/` directory to Railway
2. It auto-fetches Nifty + VIX + prevClose
3. Provides `/market` and `/history` endpoints

### 2. Android Tablet App (Frontend)
1. Open `android-kotlin/` in Android Studio
2. Ensure `API_BASE_URL` in `build.gradle.kts` matches your Railway URL
3. Build APK and install on tablet

---

## Project Structure

```
optionSelector/
├── server/             ← Python FastAPI backend (Railway)
│   ├── main.py
│   └── requirements.txt
├── android-kotlin/     ← Native Android app (Jetpack Compose)
│   └── app/src/main/
└── index.html          ← Legacy web version backup
```

---

## Trader Profile

- **Broker:** xyz + Sensibull
- **Index:** Nifty 50 only | Lot size: 65 shares
- **Portfolio:** xyz
- **Location:** xyz (xyz = IST − 1.5 hours)
- **Mode:** Paper trading → live after consistency

---

*Built with ❤️ for xyz's daily trading routine*
