# Long Strangle — Reference Guide

## When to Use
- **VIX:** < 16 (below 14 = aggressive buy)
- **Outlook:** Expecting a BIG move in either direction
- **Triggers:** Budget day, RBI policy, elections, Q4 results, global events
- **Entry:** 9:00–11:00 AM Dubai (10:30 AM–12:30 PM IST)

---

## Strike Formula

```
Distance = 1.5% from current Nifty (both sides)

BUY PE = Nifty × (1 - 1.5%) → round to nearest 50
BUY CE = Nifty × (1 + 1.5%) → round to nearest 50

CRITICAL RULE: Both legs MUST cost < ₹150 each
If either leg > ₹150 → go further OTM until < ₹150
```

---

## Example @ Nifty 24,350 / VIX 14.5

```
BUY PE = 24,350 × 0.985 = 23,985 → 24,000
BUY CE = 24,350 × 1.015 = 24,715 → 24,700

Check PE price: < ₹150? ✅
Check CE price: < ₹150? ✅
```

---

## VIX vs Premium Guide

| VIX Range | Est. Premium/leg | Action |
|-----------|-----------------|--------|
| 10–12 | ₹60–80 | BUY AGGRESSIVELY 🔥 |
| 12–14 | ₹80–120 | BUY CONFIDENTLY ✅ |
| 14–16 | ₹120–150 | BUY NORMALLY ✅ |
| 16–17 | ₹150–180 | CAUTION ⚡ — verify < ₹150 |
| > 17 | > ₹180 | SKIP — Too expensive ❌ |

---

## Profit / Loss Profile

- **Profit if:** Nifty moves > (1.5% + premium paid) either direction
- **Max loss:** 100% of premium paid (Nifty stays flat)
- **Upside:** Unlimited (on big move)
- **Break-even:** Upper BE = CE strike + total premium | Lower BE = PE strike - total premium

---

## Exit Rules

- ✅ **EXIT when one leg = 2× its cost** (double the winning leg)
- ✅ **EXIT before Thursday expiry** — never hold to zero
- 🔴 **STOP LOSS:** 50% of total premium paid
- ⚠️ If one leg decays badly → exit the whole strangle

---

## Real Trade History (May 2026)

| Date | VIX | Entry | Result | Lesson |
|------|-----|-------|--------|--------|
| May 13 | 19.0 | PE @ ₹311 | -₹9,435 | Never buy > ₹150! VIX rule broken. |
| May 19 | 16.5 | Correct | +₹5,853 | Both legs expired ITM — perfect! |

---

## Common Mistakes

| Mistake | Real Cost |
|---------|-----------|
| Buying when VIX > 17 | May 13: -₹9,435 |
| Paying > ₹150 per leg | Break-even too far to reach |
| Entering before 10:30 AM IST | May 12: ₹10-15K extra slippage |
| Holding into weekend | Gap risk Monday open |
| Buying before calm period | Time decay kills position |
