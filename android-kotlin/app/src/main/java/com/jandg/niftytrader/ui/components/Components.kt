package com.jandg.niftytrader.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jandg.niftytrader.logic.*
import com.jandg.niftytrader.ui.theme.*

// ── Reusable Card container ────────────────────────────────
@Composable
fun NiftyCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, BgBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        content = content,
    )
}

// ── VIX Meter ─────────────────────────────────────────────
@Composable
fun VixMeter(vix: Double) {
    val min = 8f; val max = 29f
    val pct = ((vix - min) / (max - min)).coerceIn(0.0, 1.0).toFloat()

    val animPct by animateFloatAsState(
        targetValue = pct,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "vix_cursor"
    )

    val (tag, tagColor) = when {
        vix < 14 -> "🟢 STRANGLE — Best PP buy" to Green
        vix < 16 -> "🟢 STRANGLE — Good entry" to Green
        vix < 18 -> "🔵 IRON CONDOR ✅" to Cyan
        vix < 20 -> "🟡 BULL PUT SPREAD — Caution" to Gold
        else     -> "🔴 SKIP income — PP urgent" to Red
    }

    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("VIX METER", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text("%.2f".format(vix), fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = tagColor)
        }
        Spacer(Modifier.height(12.dp))

        // Gradient bar
        Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))) {
            Box(modifier = Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Green, Cyan, Gold, Orange, Red))
            ))
            // Cursor
            Box(modifier = Modifier
                .offset(x = (animPct * 280).dp - 5.dp)
                .size(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .align(Alignment.CenterStart)
            )
        }

        // Zone labels
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("8", "14", "16", "18", "20", "29").forEach {
                Text(it, fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(tag, fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = tagColor)
    }
}

// ── Gate Checks ────────────────────────────────────────────
@Composable
fun GateChecksCard(gates: List<Gate>, verdict: TradeVerdict) {
    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("PRE-TRADE GATES", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(10.dp))
        gates.forEachIndexed { i, gate ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(gate) {
                kotlinx.coroutines.delay(i * 80L)
                visible = true
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = visible,
                enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInHorizontally(),
            ) {
                GateRow(gate = gate)
            }
            if (i < gates.lastIndex) HorizontalDivider(color = BgBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))
        }
    }
}

@Composable
fun GateRow(gate: Gate) {
    val (icon, iconColor, bg) = when (gate.status) {
        GateStatus.PASS -> Triple("✅", Green,  Color(0x1510B981))
        GateStatus.WARN -> Triple("⚡", Gold,   Color(0x15F59E0B))
        GateStatus.FAIL -> Triple("❌", Red,    Color(0x15EF4444))
    }
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(icon, fontSize = 16.sp)
        Column {
            Text(gate.label, fontFamily = Syne, fontWeight = FontWeight.Bold,
                fontSize = 12.sp, color = iconColor)
            Text(gate.value, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
        }
    }
}

// ── Trade Card ─────────────────────────────────────────────
@Composable
fun TradeCardComposable(calc: TradeCalc, lotSize: Int) {
    val headerColor = when (calc.strategy) {
        Strategy.IRON_CONDOR     -> Cyan
        Strategy.LONG_STRANGLE   -> Purple
        Strategy.BULL_PUT_SPREAD -> Gold
        Strategy.SKIP            -> Red
    }

    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Header
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(StrategyEngine.strategyName(calc.strategy),
                fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, color = headerColor)
            Column(horizontalAlignment = Alignment.End) {
                Text(calc.expiryLabel, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
                Text("${calc.daysToExpiry} days to expiry", fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
            }
        }
        HorizontalDivider(color = BgBorder, modifier = Modifier.padding(vertical = 10.dp))

        // Legs
        calc.legs.forEach { leg -> LegRow(leg = leg) ; Spacer(Modifier.height(6.dp)) }

        HorizontalDivider(color = BgBorder, modifier = Modifier.padding(vertical = 8.dp))

        // Financials
        if (calc.strategy == Strategy.IRON_CONDOR || calc.strategy == Strategy.BULL_PUT_SPREAD) {
            FinRow("Net Credit / share", "₹${calc.netCredit}", Green)
            FinRow("Per lot ($lotSize shares)", "₹${"%,d".format(calc.creditPerLot)}", Green)
            HorizontalDivider(color = BgBorder, modifier = Modifier.padding(vertical = 6.dp))
            FinRow("Max Profit", "₹${"%,d".format(calc.maxProfit)}", Green)
            FinRow("50% Target ✅ EXIT HERE", "₹${"%,d".format(calc.target50)}", Cyan)
            FinRow("Stop Loss (2× credit)", "₹${"%,d".format(calc.stopLoss)}", Red)
            HorizontalDivider(color = BgBorder, modifier = Modifier.padding(vertical = 6.dp))
            if (calc.lowerBE > 0) FinRow("Lower Breakeven", "${"%,d".format(calc.lowerBE)} (-${"%.2f".format(calc.lowerBEpct)}%)", TextSecondary)
            if (calc.upperBE > 0) FinRow("Upper Breakeven", "${"%,d".format(calc.upperBE)} (+${"%.2f".format(calc.upperBEpct)}%)", TextSecondary)
        } else if (calc.strategy == Strategy.LONG_STRANGLE) {
            FinRow("Total Debit / share", "₹${calc.totalDebit}", Orange)
            FinRow("Per lot ($lotSize shares)", "₹${"%,d".format(calc.debitPerLot)}", Orange)
            HorizontalDivider(color = BgBorder, modifier = Modifier.padding(vertical = 6.dp))
            FinRow("Max Loss", "₹${"%,d".format(calc.stopLoss)}", Red)
            FinRow("Lower Breakeven", "${"%,d".format(calc.lowerBE)} (-${"%.2f".format(calc.lowerBEpct)}%)", TextSecondary)
            FinRow("Upper Breakeven", "${"%,d".format(calc.upperBE)} (+${"%.2f".format(calc.upperBEpct)}%)", TextSecondary)
        }

        // Warning / Sensibull note
        if (calc.warning.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(calc.warning, fontFamily = SpaceMono, fontSize = 10.sp,
                color = if (calc.warning.startsWith("✅")) Green else Gold)
        }
        Spacer(Modifier.height(6.dp))
        Text(calc.sensibullNote, fontFamily = SpaceMono, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
fun LegRow(leg: Leg) {
    val (badgeColor, badgeBg) = if (leg.action == "BUY")
        Green to Color(0x1510B981)
    else
        Red to Color(0x15EF4444)

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BgMid)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(leg.action, fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                    fontSize = 11.sp, color = badgeColor)
            }
            Column {
                Text("${"%,d".format(leg.strike)} ${leg.optType}",
                    fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, color = TextPrimary)
                Text(leg.desc, fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹${leg.estPrice}", fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                fontSize = 13.sp, color = TextSecondary)
            Text("est. price", fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun FinRow(label: String, value: String, valueColor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
        Text(value, fontFamily = SpaceMono, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = valueColor)
    }
}

// ── PP Signal ──────────────────────────────────────────────
@Composable
fun PpSignalCard(signal: PpSignal) {
    val (bg, border, textCol) = when (signal.level) {
        PpLevel.BEST, PpLevel.GOOD -> Triple(Color(0xFF0D2B1E), Color(0x5010B981), Green)
        PpLevel.OK                 -> Triple(Color(0xFF1A1A0A), Color(0x50F59E0B), Gold)
        PpLevel.WAIT, PpLevel.SKIP -> Triple(Color(0xFF1A0F0A), Color(0x50F59E0B), Gold)
        PpLevel.URGENT             -> Triple(Color(0xFF1A0A0A), Color(0x50EF4444), Red)
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Column {
            Text("🛡️ PROTECTIVE PUT SIGNAL", fontFamily = Syne, fontWeight = FontWeight.Bold,
                fontSize = 11.sp, color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            Text(signal.message, fontFamily = SpaceMono, fontSize = 12.sp, color = textCol)
            Spacer(Modifier.height(4.dp))
            Text("Portfolio: ₹30.41L | 2 lots Sep PUT spread",
                fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        }
    }
}
