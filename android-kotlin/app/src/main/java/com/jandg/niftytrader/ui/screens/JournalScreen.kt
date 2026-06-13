package com.jandg.niftytrader.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.jandg.niftytrader.ui.theme.*
import com.jandg.niftytrader.viewmodel.TradeViewModel

@Composable
fun JournalScreen(vm: TradeViewModel) {
    val trades by vm.closedTrades.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Stats
    val totalPnl = trades.mapNotNull { it.pnl }.sum()
    val wins = trades.count { (it.pnl ?: 0.0) > 0 }
    val winRate = if (trades.isNotEmpty()) wins * 100 / trades.size else 0

    Box(Modifier.fillMaxSize().background(BgDeep)) {
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Text("TRADE JOURNAL", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp, color = TextPrimary, modifier = Modifier.padding(bottom = 4.dp))
            }

            // Stats row
            item {
                NiftyCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatItem("TOTAL", "${trades.size}", Cyan)
                        StatItem("NET P&L", "₹${"%,d".format(totalPnl.toInt())}",
                            if (totalPnl >= 0) Green else Red)
                        StatItem("WIN RATE", "$winRate%",
                            if (winRate >= 50) Green else Gold)
                        StatItem("WINS", "$wins / ${trades.size}", Green)
                    }
                }
            }

            // Trade list
            items(trades) { trade -> TradeRow(trade = trade, onDelete = { vm.deleteTrade(trade) }) }

            if (trades.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No trades yet. Tap + to log your first trade.",
                            fontFamily = SpaceMono, fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = Cyan, contentColor = BgDeep,
        ) { Icon(Icons.Default.Add, "Add trade") }
    }

    if (showAddDialog) {
        AddTradeDialog(onDismiss = { showAddDialog = false }, onSave = { trade ->
            vm.addTrade(trade)
            showAddDialog = false
        })
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        Spacer(Modifier.height(2.dp))
        Text(value, fontFamily = SpaceMono, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
    }
}

@Composable
fun TradeRow(trade: TradeEntity, onDelete: () -> Unit) {
    val pnl = trade.pnl ?: 0.0
    val pnlColor = when {
        pnl > 0  -> Green
        pnl < 0  -> Red
        else     -> TextSecondary
    }
    val pnlSign = if (pnl >= 0) "+" else ""

    NiftyCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(trade.strategy, fontFamily = Syne, fontWeight = FontWeight.Bold,
                    fontSize = 13.sp, color = TextPrimary)
                Text(trade.date, fontFamily = SpaceMono, fontSize = 10.sp, color = TextMuted)
                Text("Nifty ${"%,.0f".format(trade.entryNifty)} | VIX ${"%.1f".format(trade.entryVix)}",
                    fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
                if (trade.lesson.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("💡 ${trade.lesson}", fontFamily = SpaceMono, fontSize = 10.sp, color = Gold)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$pnlSign₹${"%,d".format(pnl.toInt())}",
                    fontFamily = SpaceMono, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = pnlColor)
                Text(trade.expiry, fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun AddTradeDialog(onDismiss: () -> Unit, onSave: (TradeEntity) -> Unit) {
    var date by remember { mutableStateOf("") }
    var strategy by remember { mutableStateOf("Iron Condor") }
    var nifty by remember { mutableStateOf("") }
    var vix by remember { mutableStateOf("") }
    var pnl by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var lesson by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BgCard,
        title = { Text("Log Trade", fontFamily = Syne, fontWeight = FontWeight.ExtraBold, color = TextPrimary) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NiftyTextField("Date (YYYY-MM-DD)", date) { date = it }
                val strategies = listOf("Iron Condor", "Long Strangle", "Bull Put Spread", "Protective Put", "Other")
                Text("Strategy", fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
                strategies.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = strategy == s, onClick = { strategy = s },
                            colors = RadioButtonDefaults.colors(selectedColor = Cyan))
                        Text(s, fontFamily = Syne, fontSize = 12.sp, color = TextPrimary)
                    }
                }
                NiftyTextField("Nifty at entry", nifty) { nifty = it }
                NiftyTextField("VIX at entry", vix) { vix = it }
                NiftyTextField("Final P&L (₹, blank if open)", pnl) { pnl = it }
                NiftyTextField("Expiry (YYYY-MM-DD)", expiry) { expiry = it }
                NiftyTextField("Notes", notes) { notes = it }
                NiftyTextField("Lesson", lesson) { lesson = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(TradeEntity(
                        date = date,
                        strategy = strategy,
                        entryNifty = nifty.toDoubleOrNull() ?: 0.0,
                        entryVix = vix.toDoubleOrNull() ?: 0.0,
                        credit = 0.0, debit = 0.0,
                        expiry = expiry,
                        pnl = pnl.toDoubleOrNull(),
                        notes = notes, lesson = lesson,
                        isOpen = pnl.isBlank(),
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan),
            ) { Text("Save", color = BgDeep, fontFamily = Syne, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
    )
}

@Composable
fun NiftyTextField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onChange, label = { Text(label, fontSize = 11.sp) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cyan, unfocusedBorderColor = BgBorder,
            focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
            focusedLabelColor = Cyan, unfocusedLabelColor = TextSecondary,
            cursorColor = Cyan,
        ),
        textStyle = LocalTextStyle.current.copy(fontFamily = SpaceMono, fontSize = 12.sp),
        singleLine = true,
    )
}
