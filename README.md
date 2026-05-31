# NiftyTrader — Options Strategy Selector

> **Juli's personal NSE Nifty 50 options strategy selector & trade manager**
> Built for daily pre-market analysis from Dubai (GST) → IST conversion included.

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
- 📱 **PWA** — Installable on Android tablet (Add to Home Screen)

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

- ❌ Never enter before 9:00 AM Dubai (10:30 AM IST)
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

No installation needed — pure HTML/JS/CSS single file.

1. Open `index.html` in Chrome/Edge
2. Enter VIX + Nifty → hit **Analyze Setup**
3. Get trade card + gate checks instantly

### Android Tablet (PWA Install)
1. Open in Chrome → tap 3-dot menu
2. Select **"Add to Home Screen"**
3. Launches as standalone app, works offline

### Claude AI Integration
1. Go to **Settings** tab
2. Enter your Anthropic API key (`sk-ant-...`)
3. Use the **AI tab** for custom analysis

---

## Project Structure

```
optionSelector/
├── index.html          ← Full app (single file)
├── manifest.json       ← PWA manifest
└── references/
    ├── iron-condor.md
    ├── long-strangle.md
    ├── bull-put-spread.md
    └── protective-put.md
```

---

## Trader Profile

- **Broker:** SBI Securities + Sensibull
- **Index:** Nifty 50 only | Lot size: 65 shares
- **Portfolio:** ₹30.41L
- **Location:** Dubai, UAE (GST = IST − 1.5 hours)
- **Mode:** Paper trading → live after consistency

---

*Built with ❤️ for Juli's daily trading routine*
