package com.jandg.niftytrader.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jandg.niftytrader.ui.components.NiftyCard
import com.jandg.niftytrader.ui.theme.*

data class RefStrategy(val emoji: String, val name: String, val color: Color, val content: String)

@Composable
fun RefsScreen() {
    val scroll = rememberScrollState()
    var selected by remember { mutableStateOf(0) }

    val refs = listOf(
        RefStrategy("🏗️", "Iron Condor", Cyan, IC_CONTENT),
        RefStrategy("↔️", "Long Strangle", Purple, LS_CONTENT),
        RefStrategy("🐂", "Bull Put Spread", Gold, BPS_CONTENT),
        RefStrategy("🛡️", "Protective Put", Green, PP_CONTENT),
    )

    Column(Modifier.fillMaxSize().background(BgDeep)) {
        // Tab row
        ScrollableTabRow(
            selectedTabIndex = selected,
            containerColor = BgMid,
            contentColor = Cyan,
            edgePadding = 8.dp,
        ) {
            refs.forEachIndexed { i, ref ->
                Tab(
                    selected = i == selected,
                    onClick = { selected = i },
                    text = {
                        Text("${ref.emoji} ${ref.name}", fontFamily = Syne,
                            fontWeight = if (i == selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp, color = if (i == selected) ref.color else TextSecondary)
                    }
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val ref = refs[selected]
            NiftyCard {
                Text("${ref.emoji} ${ref.name}", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp, color = ref.color)
                Spacer(Modifier.height(12.dp))
                Text(ref.content, fontFamily = SpaceMono, fontSize = 11.sp,
                    color = TextSecondary, lineHeight = 18.sp)
            }
            DailyRoutineCard()
        }
    }
}

@Composable
fun DailyRoutineCard() {
    NiftyCard {
        Text("📋 DAILY TRADING ROUTINE", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp, color = Gold)
        Spacer(Modifier.height(10.dp))
        val steps = listOf(
            "8:30 AM Dubai" to "Wake up. Open NiftyTrader app.",
            "8:45 AM Dubai" to "Check VIX level. Check Asia markets (SGX Nifty).",
            "9:00 AM Dubai" to "Market opens (9:15 IST). Do NOT enter yet.",
            "9:00-9:15 AM" to "Watch Nifty open. Note gap direction.",
            "9:15 AM Dubai" to "Entry window opens (10:30 IST). Verify gate checks.",
            "9:15-11:00 AM" to "Analyze setup. Check strategy. Calculate strikes on Sensibull.",
            "11:00 AM Dubai" to "LAST entry time (12:30 IST). After this = NO new trades.",
            "Ongoing" to "Monitor P&L. Set 50% profit alert.",
            "2:00 PM Dubai" to "Market closes (3:30 IST). Close positions or leave for theta.",
        )
        steps.forEach { (time, action) ->
            Row(Modifier.padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(time, fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                    fontSize = 10.sp, color = Cyan, modifier = Modifier.width(110.dp))
                Text(action, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

// ── Reference content strings ──────────────────────────────

private val IC_CONTENT = """
WHEN TO USE
• VIX: 16–18 (core strategy zone)
• Outlook: Nifty stays range-bound
• Best: 8–14 days to expiry

STRIKE FORMULA
OTM % = 1.8% (VIX 16–17) | 2.0% (VIX 17–18)

SELL PE = Nifty × (1 - OTM%) → round to 50
BUY  PE = SELL PE - 500
SELL CE = Nifty × (1 + OTM%) → round to 50
BUY  CE = SELL CE + 500

RULE: SELL PE < Nifty < SELL CE (sandwich rule)

ENTRY SEQUENCE (SBI Securities)
1. SELL CALL (upper short leg)
2. BUY CALL (upper wing)
3. SELL PUT (lower short leg)
4. BUY PUT (lower wing)

EXIT RULES
✅ EXIT at 50% of max profit — NO EXCEPTIONS
🔴 STOP LOSS = 2× credit received
⏰ TIME EXIT: Thursday before expiry week
⚠️ Exit if Nifty touches either SELL strike

MISTAKES TO AVOID
❌ Entering before 10:30 AM IST
❌ Using VIX > 18
❌ Selling ITM strikes
❌ Holding into expiry Thursday
""".trimIndent()

private val LS_CONTENT = """
WHEN TO USE
• VIX: < 16 (below 14 = aggressive buy)
• Expecting a BIG move either direction
• Triggers: RBI policy, budget, elections

STRIKE FORMULA
Distance = 1.5% from Nifty (both sides)

BUY PE = Nifty × 0.985 → round to 50
BUY CE = Nifty × 1.015 → round to 50

CRITICAL: Both legs MUST cost < ₹150 each
If > ₹150 → go further OTM until < ₹150

VIX GUIDE
VIX 10-12: ~₹60-80/leg  → BUY AGGRESSIVELY
VIX 12-14: ~₹80-120/leg → BUY CONFIDENTLY
VIX 14-16: ~₹120-150/leg → BUY NORMALLY
VIX > 17:  > ₹180/leg   → SKIP

EXIT RULES
✅ EXIT when one leg = 2× its cost
✅ EXIT before Thursday expiry
🔴 STOP LOSS: 50% of total premium paid

MAY 2026 LESSONS
May 13: VIX 19, PE @ ₹311 → -₹9,435 ❌
  LESSON: Never buy at VIX > 17!
May 19: VIX 16.5, correct entry → +₹5,853 ✅
  LESSON: Both legs expired ITM — perfect!
""".trimIndent()

private val BPS_CONTENT = """
WHEN TO USE
• VIX: 18–20 (caution mode)
• Mildly bullish — Nifty holds support
• NOT if market is in clear downtrend

STRIKE FORMULA
SELL PE = Nifty × (1 - 2.5%) → round to 50
BUY  PE = SELL PE - 300

MINIMUM net credit = ₹40/share
If credit < ₹40 → SKIP the trade

EXAMPLE @ Nifty 24,350 / VIX 18.5
SELL PE = 24,350 × 0.975 = 23,750
BUY  PE = 23,750 - 300 = 23,450
Net Credit: ~₹95/share = ₹6,175/lot

EXIT RULES
✅ EXIT at 50% of credit received
🔴 EXIT if Nifty approaches SELL PE
⚠️ Close early if Nifty drops > 2.5%

VS IRON CONDOR
BPS = put side only, lower credit, lower risk
Use BPS when VIX 18-20 (too volatile for IC)
""".trimIndent()

private val PP_CONTENT = """
PURPOSE
Insure ₹30.41L portfolio against crash.
Cost = ₹5,300/month. Worth every rupee.

⚠️ CRITICAL: OPPOSITE TO INCOME TRADES
INCOME:   SELL when VIX HIGH (more premium)
PP:       BUY  when VIX LOW  (cheaper!)
Best time to buy insurance = when cheap!

VIX SIGNAL
< 14:    🟢 BEST — Buy immediately!
14–16:   🟢 GOOD — Buy now
16–17:   🟡 OK — Slightly elevated
17–18:   🟠 WAIT — Wait for dip below 17
18–20:   🔴 TOO EXPENSIVE — Don't buy
> 20:    🚨 URGENT — Buy regardless!

SETUP (Put Spread)
BUY  PUT = Nearest 500 below Nifty (e.g. 23,000)
SELL PUT = BUY strike - 1,000 (e.g. 22,000)
Lots = 2 (covers ~₹30L portfolio)
Expiry = Sep 2026 (132 days) ← RECOMMENDED

COST
Net debit: ~₹180-220/share
2 lots × 65 shares = 130 shares
Total: ~₹23,400 = ₹5,300/month
Daily cost: ₹177/day

ROLLING
Roll 30 days before expiry.
Roll when IV is LOW, not during panic.
Cost to roll: ~₹2,000-5,000/cycle.
""".trimIndent()
