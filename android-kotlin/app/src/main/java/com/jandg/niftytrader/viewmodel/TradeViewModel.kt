package com.jandg.niftytrader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jandg.niftytrader.data.TradeDao
import com.jandg.niftytrader.data.TradeEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val dao: TradeDao,
) : ViewModel() {

    val allTrades = dao.allTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val openPosition = dao.openPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val closedTrades = dao.closedTrades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTrade(trade: TradeEntity) = viewModelScope.launch { dao.insert(trade) }

    fun updateTrade(trade: TradeEntity) = viewModelScope.launch { dao.update(trade) }

    fun deleteTrade(trade: TradeEntity) = viewModelScope.launch { dao.delete(trade) }

    fun closeTrade(trade: TradeEntity, finalPnl: Double) = viewModelScope.launch {
        dao.update(trade.copy(pnl = finalPnl, isOpen = false))
    }

    fun seedHistoricalTrades() = viewModelScope.launch {
        // Pre-seed Juli's May 2026 paper trades
        val historical = listOf(
            TradeEntity(date="2026-05-12", strategy="Long Strangle", entryNifty=23500.0, entryVix=17.2,
                credit=0.0, debit=9100.0, expiry="2026-05-15", pnl=4599.0,
                notes="Won despite 7:58AM Dubai entry ❌. Both legs profitable anyway!",
                lesson="Won despite bad entry"),
            TradeEntity(date="2026-05-13", strategy="Long Strangle", entryNifty=23600.0, entryVix=19.0,
                credit=0.0, debit=20215.0, expiry="2026-05-15", pnl=-9435.0,
                notes="VIX 19 ❌ — PE bought @ ₹311, far too expensive. Classic high-VIX mistake.",
                lesson="Never buy at VIX > 17"),
            TradeEntity(date="2026-05-19", strategy="Long Strangle", entryNifty=23450.0, entryVix=16.5,
                credit=0.0, debit=12350.0, expiry="2026-05-22", pnl=5853.0,
                notes="Both legs expired ITM! Perfect strangle entry ✅",
                lesson="Both legs ITM — perfect!"),
            TradeEntity(date="2026-05-20", strategy="Iron Condor", entryNifty=23627.0, entryVix=18.7,
                credit=7400.0, debit=0.0, expiry="2026-05-22", pnl=3200.0,
                notes="First correct IC entry at 10:22AM IST ✅. Exited at 43% profit.",
                lesson="First correct IC!"),
        )
        historical.forEach { dao.insert(it) }
    }
}
