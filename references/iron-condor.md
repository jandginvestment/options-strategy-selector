# Iron Condor — Reference Guide

## When to Use
- **VIX:** 16–18 (core strategy zone)
- **Outlook:** Nifty stays range-bound (sideways market)
- **Best:** 8–14 days to expiry
- **Entry:** 9:00–11:00 AM Dubai (10:30 AM–12:30 PM IST)

---

## Strike Formula

```
OTM % = 1.8%  (VIX 16–17)
OTM % = 2.0%  (VIX 17–18)

SELL PE = Nifty × (1 - OTM%) → round to nearest 50
BUY  PE = SELL PE - 500
SELL CE = Nifty × (1 + OTM%) → round to nearest 50
BUY  CE = SELL CE + 500

MUST: SELL PE < Nifty < SELL CE  ← sandwich rule!
```

---

## Example @ Nifty 24,350 / VIX 17.2

```
OTM%    = 1.8%
SELL PE = 24,350 × 0.982 = 23,912 → 23,900
BUY  PE = 23,900 - 500            = 23,400
SELL CE = 24,350 × 1.018 = 24,789 → 24,800
BUY  CE = 24,800 + 500            = 25,300
```

---

## Risk / Reward

| Item | Amount |
|------|--------|
| Max Profit | Net credit × lot size |
| Max Loss | Wing width - net credit |
| Risk-Reward | ~1:3 (risk ₹3 to make ₹1) |

---

## Exit Rules

- ✅ **EXIT at 50% of max profit** — NO EXCEPTIONS
- 🔴 **STOP LOSS = 2× credit received**
- ⏰ **TIME EXIT:** Thursday before expiry week
- ⚠️ **STRIKE BREACH:** Exit if Nifty touches either SELL strike

---

## Sensibull Verification Checklist

- [ ] Tent-shaped payoff diagram
- [ ] Bottom shows **"GET ₹X"** (not "PAY")
- [ ] Both SELL strikes are OTM
- [ ] Both breakevens are **>1.5%** from current Nifty

---

## SBI Securities Entry Sequence

1. SELL CALL (upper short leg)
2. BUY CALL (upper wing/hedge)
3. SELL PUT (lower short leg)
4. BUY PUT (lower wing/hedge)

---

## Common Mistakes to Avoid

| Mistake | Consequence |
|---------|-------------|
| Entering before 10:30 AM IST | IV not settled, premiums wrong |
| VIX > 18 | IV crush already happened, limited edge |
| Selling ITM strikes | Immediate intrinsic loss |
| Ignoring VIX direction | Rising VIX = bad for short positions |
| Holding into expiry Thursday | Gamma risk, violent swings |
