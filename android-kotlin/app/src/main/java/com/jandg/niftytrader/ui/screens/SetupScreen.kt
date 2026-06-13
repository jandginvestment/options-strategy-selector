package com.jandg.niftytrader.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jandg.niftytrader.logic.*
import com.jandg.niftytrader.ui.components.*
import com.jandg.niftytrader.ui.theme.*
import com.jandg.niftytrader.viewmodel.MarketUiState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SetupScreen(
    state: MarketUiState,
    onRefresh: () -> Unit,
    onTrackPosition: () -> Unit,
    onLogTrade: () -> Unit,
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .verticalScroll(scroll)
            .padding(bottom = 80.dp),
    ) {
        // ── Header ─────────────────────────────────────────
        MarketHeader(state = state, onRefresh = onRefresh)

        // ── Phase Banner ───────────────────────────────────
        PhaseBanner(phase = state.phase)

        Spacer(Modifier.height(12.dp))

        // ── Market Data Card ────────────────────────────────
        state.market?.let { mkt ->
            MarketDataCard(market = mkt)
        } ?: run {
            if (state.isLoading) LoadingCard()
            else ErrorCard(state.error ?: "Tap refresh to load market data", onRefresh)
        }

        Spacer(Modifier.height(12.dp))

        // ── VIX Meter ──────────────────────────────────────
        state.market?.let { VixMeter(vix = it.vix) }

        Spacer(Modifier.height(12.dp))

        // ── Gate Checks ─────────────────────────────────────
        if (state.gates.isNotEmpty()) {
            GateChecksCard(gates = state.gates, verdict = state.verdict)
            Spacer(Modifier.height(12.dp))
        }

        // ── Strategy Result ─────────────────────────────────
        state.market?.let {
            StrategyResultCard(
                strategy = state.strategy,
                vix      = it.vix,
                verdict  = state.verdict,
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Trade Card ──────────────────────────────────────
        state.tradeCalc?.let {
            TradeCardComposable(calc = it, lotSize = state.lotSize)
            Spacer(Modifier.height(12.dp))
        }

        // ── PP Signal ───────────────────────────────────────
        state.ppSignal?.let {
            PpSignalCard(signal = it)
            Spacer(Modifier.height(12.dp))
        }

        // ── Action Buttons ──────────────────────────────────
        if (state.market != null) {
            ActionButtons(
                onTrack = onTrackPosition,
                onLog   = onLogTrade,
            )
        }
    }
}

@Composable
fun MarketHeader(state: MarketUiState, onRefresh: () -> Unit) {
    // Live clock ticking every second
    var dxbTime by remember { mutableStateOf("") }
    var istTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val dxb = ZonedDateTime.now(TimeEngine.DXB)
            val ist = ZonedDateTime.now(TimeEngine.IST)
            val fmt = DateTimeFormatter.ofPattern("h:mm a")
            dxbTime = dxb.format(fmt)
            istTime = ist.format(fmt)
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgMid)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("NiftyTrader", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp, color = Cyan)
                Text("Juli's NSE Options", fontFamily = Syne, fontSize = 11.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(dxbTime, fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = TextPrimary)
                Text("DXB  |  $istTime IST", fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
            }
            IconButton(onClick = onRefresh, enabled = !state.isLoading) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp),
                        color = Cyan, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh",
                        tint = Cyan, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun PhaseBanner(phase: MarketPhase) {
    val (bgColor, textColor) = when (phase) {
        MarketPhase.ENTRY   -> Color(0xFF0D2B1E) to Green
        MarketPhase.WAIT    -> Color(0xFF2B1F0A) to Gold
        MarketPhase.LATE    -> Color(0xFF2B1F0A) to Gold
        MarketPhase.PRE     -> Color(0xFF1A1A2E) to TextSecondary
        else                -> Color(0xFF1A0A0A) to Red
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(phase.emoji, fontSize = 16.sp)
            Text(
                text = when (phase) {
                    MarketPhase.ENTRY   -> "✅ ENTRY WINDOW OPEN — 9:00–11:00 AM Dubai (10:30–12:30 IST)"
                    MarketPhase.WAIT    -> "⚡ Wait for market settlement — entry at 9:00 AM Dubai"
                    MarketPhase.LATE    -> "⚠️ Late session — reduced theta, consider smaller size"
                    MarketPhase.PRE     -> "⏳ Pre-market — NSE opens 7:45 AM Dubai (9:15 IST)"
                    MarketPhase.WEEKEND -> "🔴 Weekend — market closed"
                    MarketPhase.CLOSED  -> "🔴 Market closed — NSE closes 2:00 PM Dubai"
                },
                fontFamily = SpaceMono, fontSize = 11.sp, color = textColor,
            )
        }
    }
}

@Composable
fun MarketDataCard(market: com.jandg.niftytrader.data.MarketData) {
    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("MARKET DATA", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            MarketDataItem("NIFTY 50", "%,.2f".format(market.nifty), Cyan)
            MarketDataItem("INDIA VIX", "%.2f".format(market.vix),
                when {
                    market.vix < 16 -> Green
                    market.vix < 18 -> Cyan
                    market.vix < 20 -> Gold
                    else            -> Red
                })
            val sign = if (market.gapPct >= 0) "+" else ""
            MarketDataItem("GAP",
                "$sign${"%.2f".format(market.gapPct)}%",
                if (market.gapPct >= 0) Green else Red)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            MarketDataItem("PREV CLOSE", "%,.2f".format(market.prevClose), TextSecondary)
            MarketDataItem("BEST EXPIRY", market.expiryLabel, Gold)
            MarketDataItem("DAYS TO EXP", "${market.daysToExpiry}d",
                if (market.daysToExpiry in 6..14) Green else Gold)
        }
        if (market.timestamp.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text("Updated: ${market.timestamp.take(19).replace("T"," ")} IST",
                fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun MarketDataItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        Spacer(Modifier.height(2.dp))
        Text(value, fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
            fontSize = 14.sp, color = valueColor)
    }
}

@Composable
fun StrategyResultCard(strategy: Strategy, vix: Double, verdict: TradeVerdict) {
    val (bg, border, textCol) = when (verdict) {
        TradeVerdict.GO      -> Triple(Color(0xFF0D2B1E), Color(0xFF10B981), Green)
        TradeVerdict.CAUTION -> Triple(Color(0xFF2B1F0A), Color(0xFFF59E0B), Gold)
        TradeVerdict.NO_GO   -> Triple(Color(0xFF1A0A0A), Color(0xFFEF4444), Red)
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Column {
            Text("${StrategyEngine.strategyEmoji(strategy)} ${StrategyEngine.strategyName(strategy)}",
                fontFamily = Syne, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = textCol)
            Text("VIX ${"%.2f".format(vix)}", fontFamily = SpaceMono, fontSize = 11.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text(
                text = when (verdict) {
                    TradeVerdict.GO      -> "🟢 GO — All gates passed. Ready to trade!"
                    TradeVerdict.CAUTION -> "🟡 PROCEED WITH CAUTION — Check warnings above"
                    TradeVerdict.NO_GO   -> "🔴 NO-GO — One or more gates failed"
                },
                fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textCol,
            )
        }
    }
}

@Composable
fun ActionButtons(onTrack: () -> Unit, onLog: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onTrack,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Purple),
            shape = RoundedCornerShape(10.dp),
        ) { Text("📊 Track", fontFamily = Syne, fontWeight = FontWeight.Bold) }

        Button(
            onClick = onLog,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = BgCard),
            shape = RoundedCornerShape(10.dp),
        ) { Text("📓 Log Trade", fontFamily = Syne, fontWeight = FontWeight.Bold, color = TextSecondary) }
    }
}

@Composable
fun LoadingCard() {
    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Cyan, strokeWidth = 2.dp)
            Text("Fetching live market data from Railway API...",
                fontFamily = SpaceMono, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    NiftyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("⚠️ $message", fontFamily = SpaceMono, fontSize = 11.sp, color = Gold)
        Spacer(Modifier.height(10.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Cyan),
            shape = RoundedCornerShape(8.dp)) {
            Text("Retry", fontFamily = Syne, fontWeight = FontWeight.Bold, color = BgDeep)
        }
    }
}
