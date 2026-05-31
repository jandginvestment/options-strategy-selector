# Protective Put — Reference Guide

## Purpose

Insure Juli's ₹30.41L portfolio against a major market crash.

| Without PP | With PP |
|-----------|---------|
| 20% Nifty crash = ₹6L+ loss | Same crash = ~₹4.7L loss |
| No floor | ₹1.3L insurance payout |
| Monthly cost: ₹0 | Monthly cost: ₹5,300 |

**₹5,300/month for ₹30L of protection = worth every rupee.**

---

## ⚠️ CRITICAL: Opposite to Income Trades

```
INCOME TRADE:   SELL when VIX HIGH → collect more premium
PROTECTIVE PUT: BUY  when VIX LOW  → pay less premium

This is counter-intuitive but CORRECT.
Best time to buy insurance = when it's cheapest!
```

---

## VIX Signal Guide

| VIX Level | Signal | Action |
|-----------|--------|--------|
| < 14 | 🟢 BEST TIME | Buy immediately! Cheapest insurance available |
| 14–16 | 🟢 GOOD | Buy now. Premiums reasonable. |
| 16–17 | 🟡 ACCEPTABLE | Slightly elevated. OK to buy. |
| 17–18 | 🟠 WAIT IF POSSIBLE | Wait for dip below 17. Exception: portfolio up >15% → buy anyway |
| 18–20 | 🔴 TOO EXPENSIVE | Don't buy. Watch for next dip below 17. |
| > 20 | 🚨 URGENT OVERRIDE | Buy REGARDLESS of cost. Portfolio at risk! |

---

## Setup — Put Spread (NOT naked put)

```
BUY  PUT = Nearest 500 below current Nifty  (e.g. 23,000 PE)
SELL PUT = BUY strike - 1,000              (e.g. 22,000 PE)
Lots    = 2 (covers ~₹30L portfolio)
Expiry  = Sep 2026 (132 days) ← RECOMMENDED
```

---

## Cost Estimate

```
Net debit: ~₹180–220/share
2 lots × 65 shares = 130 shares total

Total cost:   ₹180 × 130 = ~₹23,400
Duration:     132 days
Daily cost:   ₹23,400 ÷ 132 = ₹177/day
Monthly cost: ₹177 × 30    = ~₹5,300/month
```

**Comparison:**
- Netflix = ₹649/month
- Portfolio insurance = ₹5,300/month → for ₹30L coverage ✅

---

## Protection Payout

```
If Nifty falls FROM 23,000 TO 22,000:
  Each lot pays: 1,000 pts × 65 shares = ₹65,000
  2 lots total:                        = ₹1,30,000

Approximate portfolio loss at 4% drop:  ~₹2.5L (on ₹19L equity)
Insurance payout:                       ₹1,30,000
Net loss after insurance:               ~₹1.2L (vs ₹2.5L without!) ✅
```

---

## Portfolio Coverage Math

```
Total portfolio:   ₹30.41L
Equity exposure:   ~₹19L (62% in stocks)
Nifty per lot:     65 × ~₹24,000 = ₹15.6L
2 lots total:      ₹31.2L → covers full portfolio ✅
```

---

## Expiry Selection

| Expiry | Days | Daily Cost | Recommendation |
|--------|------|-----------|----------------|
| 28 Jul 2026 | 69 days | ₹339/day | Short-term protection |
| 29 Sep 2026 | 132 days | ₹177/day | ⭐ RECOMMENDED |
| 29 Dec 2026 | 223 days | ~₹105/day | Best value (buy when cheap) |

---

## When to Buy: Timing Strategy

- ✅ **BEST:** VIX peaks and starts to FALL (fear → calm turning point)
- ✅ **GOOD:** VIX below 16 during a quiet market period
- ❌ **NOT:** While VIX is still spiking UP
- ❌ **NOT:** In the first 15 mins of a panic selloff
- **Wait for:** VIX hits intraday high → starts to turn down → BUY

---

## Rolling Strategy

> **Rule:** Roll the PP 30 days before expiry.

- Roll when: IV is relatively low (don't roll during a panic)
- Cost to roll: ~₹2,000–5,000 net per cycle
- Never let PP expire worthless if Nifty is near strike

---

## SBI Securities Setup

```
Entry (2 separate orders):
Order 1: BUY  2 lots  Sep 23,000 PE  (insurance floor)
Order 2: SELL 2 lots  Sep 22,000 PE  (reduces premium cost)

This is a PUT SPREAD — reduces cost by ~40%
vs buying naked puts
```

---

## Key Reminders

| Rule | Why |
|------|-----|
| Buy when VIX LOW | Insurance is cheapest |
| Sell a lower put to reduce cost | Cuts premium by ~40% |
| 2 lots minimum | Covers full ₹30L portfolio |
| Sep expiry preferred | Best time value balance |
| Roll 30 days before expiry | Avoid gamma risk |
