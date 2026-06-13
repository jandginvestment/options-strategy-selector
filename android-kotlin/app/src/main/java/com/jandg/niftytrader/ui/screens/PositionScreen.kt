package com.jandg.niftytrader.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jandg.niftytrader.data.TradeEntity
import com.jandg.niftytrader.ui.components.NiftyCard
import com.jandg.niftytrader.ui.screens.NiftyTextField
import com.jandg.niftytrader.ui.theme.*
import com.jandg.niftytrader.viewmodel.TradeViewModel

@Composable
fun PositionScreen(vm: TradeViewModel, currentNifty: Double) {
    val position by vm.openPosition.collectAsState()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().background(BgDeep).verticalScroll(scroll).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("OPEN POSITION", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp, color = TextPrimary)

        if (position == null) {
            NiftyCard {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No open position", fontFamily = Syne, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = TextSecondary)
                        Text("Enter a trade from the Setup screen",
                            fontFamily = SpaceMono, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        } else {
            OpenPositionCard(position = position!!, currentNifty = currentNifty, onClose = { pnl ->
                vm.closeTrade(position!!, pnl)
            })
        }
    }
}

@Composable
fun OpenPositionCard(position: TradeEntity, currentNifty: Double, onClose: (Double) -> Unit) {
    var enteredPnl by remember { mutableStateOf("") }
    var showCloseDialog by remember { mutableStateOf(false) }

    // P&L calculations
    val maxProfit = position.credit * 65.0
    val pnlEstimate = 0.0 // Simplified: actual P&L entered manually

    // Distance to strikes
    val distToSellCE = if (position.sellCE > 0) ((position.sellCE - currentNifty) / currentNifty * 100) else 0.0
    val distToSellPE = if (position.sellPE > 0) ((currentNifty - position.sellPE) / currentNifty * 100) else 0.0
    val dangerCE = distToSellCE in 0.0..1.0
    val dangerPE = distToSellPE in 0.0..1.0

    NiftyCard {
        Text(position.strategy, fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp, color = Cyan)
        Text("Entered: ${position.date} | Expiry: ${position.expiry}",
            fontFamily = SpaceMono, fontSize = 10.sp, color = TextMuted)

        Spacer(Modifier.height(12.dp))

        if (position.sellCE > 0 || position.sellPE > 0) {
            Text("STRIKE DISTANCES", fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            if (position.sellCE > 0) {
                StrikeDistanceBar(
                    label = "Nifty → SELL CE ${"%,d".format(position.sellCE.toInt())}",
                    pct = distToSellCE.coerceIn(0.0, 5.0),
                    maxPct = 5.0,
                    color = if (dangerCE) Red else Green,
                    danger = dangerCE,
                )
            }
            Spacer(Modifier.height(8.dp))
            if (position.sellPE > 0) {
                StrikeDistanceBar(
                    label = "SELL PE ${"%,d".format(position.sellPE.toInt())} → Nifty",
                    pct = distToSellPE.coerceIn(0.0, 5.0),
                    maxPct = 5.0,
                    color = if (dangerPE) Red else Green,
                    danger = dangerPE,
                )
            }
            if (dangerCE || dangerPE) {
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(Color(0x30EF4444)).padding(10.dp)) {
                    Text("⚠️ DANGER — Nifty approaching strike! Consider closing.",
                        fontFamily = SpaceMono, fontSize = 11.sp, color = Red)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = BgBorder)
        Spacer(Modifier.height(12.dp))

        // Manual P&L entry
        Text("CLOSE POSITION", fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        NiftyTextField("Final P&L (₹)", enteredPnl) { enteredPnl = it }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { showCloseDialog = true },
            enabled = enteredPnl.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Red),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text("✅ Close Position", fontFamily = Syne, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            containerColor = BgCard,
            title = { Text("Close Position?", fontFamily = Syne, fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = { Text("Final P&L: ₹${enteredPnl}", fontFamily = SpaceMono, color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = { onClose(enteredPnl.toDoubleOrNull() ?: 0.0); showCloseDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                ) { Text("Close", fontFamily = Syne, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showCloseDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
        )
    }
}

@Composable
fun StrikeDistanceBar(label: String, pct: Double, maxPct: Double, color: Color, danger: Boolean) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
            Text("${"%.2f".format(pct)}%", fontFamily = SpaceMono, fontWeight = FontWeight.Bold,
                fontSize = 10.sp, color = color)
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(BgBorder)) {
            Box(Modifier.fillMaxWidth((pct / maxPct).toFloat().coerceIn(0f, 1f))
                .fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(color))
        }
        if (danger) {
            Text("⚠️ < 1% buffer — DANGER ZONE", fontFamily = SpaceMono, fontSize = 9.sp, color = Red)
        }
    }
}
