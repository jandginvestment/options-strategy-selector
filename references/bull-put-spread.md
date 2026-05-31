# Bull Put Spread — Reference Guide

## When to Use
- **VIX:** 18–20 (caution mode, defined-risk only)
- **Outlook:** Mildly bullish — Nifty holds above support
- **NOT suitable if:** Market trending down sharply, VIX > 20
- **Entry:** 9:00–11:00 AM Dubai (10:30 AM IST) only

> **Note:** At VIX 18–20, a full Iron Condor is too risky. BPS gives you income with limited downside.

---

## Strike Formula

```
SELL PE = Nifty × (1 - 2.5%) → round to nearest 50
BUY  PE = SELL PE - 300

MINIMUM net credit = ₹40/share
If net credit < ₹40 → SKIP the trade (not worth the risk)
```

---

## Example @ Nifty 24,350 / VIX 18.5

```
SELL PE = 24,350 × 0.975 = 23,741 → 23,750
BUY  PE = 23,750 - 300            = 23,450

Est. SELL price:   ~₹145/share
Est. BUY price:    ~₹50/share
Net Credit:        ~₹95/share
Per lot (65):      ~₹6,175
Max Loss:          (300 - 95) × 65 = ~₹13,325
```

---

## Risk / Reward

| Item | Value |
|------|-------|
| Max Profit | Net credit × lot size |
| Max Loss | (Width - net credit) × lot size |
| Stop Loss | Close at 2× credit received |
| 50% Target | Half of max profit |

---

## Exit Rules

- ✅ **EXIT at 50% of credit received**
- 🔴 **EXIT if Nifty approaches the SELL PE level**
- ⚠️ **Close early** if Nifty drops > 2.5% from entry
- ⏰ **Time exit:** Thursday before expiry week

---

## BPS vs Iron Condor

| Aspect | Bull Put Spread | Iron Condor |
|--------|----------------|-------------|
| VIX zone | 18–20 | 16–18 |
| Legs | 2 (put side only) | 4 (both sides) |
| Risk | Lower (one-sided) | Higher (both sides) |
| Credit | Lower | Higher |
| Use when | Too volatile for IC | Range-bound market |

---

## Minimum Credit Check

> If `net credit < ₹40/share` → **do not trade**.
> Risk-reward becomes unfavorable.
> The VIX is elevated, which means wider bid-ask spreads and lower net credits.

---

## Common Mistakes

| Mistake | Why It Hurts |
|---------|-------------|
| Trading BPS in a downtrend | Directionally wrong |
| Accepting < ₹40 credit | Risk not worth it |
| Using when VIX > 20 | Skip income entirely at VIX > 20 |
| No stop plan if Nifty breaks support | Losses accelerate fast |
