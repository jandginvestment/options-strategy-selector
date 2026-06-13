package com.jandg.niftytrader.data

import androidx.room.*

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val strategy: String,
    val entryNifty: Double,
    val entryVix: Double,
    val credit: Double,       // net credit received (IC/BPS) or 0 (LS)
    val debit: Double,        // total premium paid (LS) or 0 (IC/BPS)
    val expiry: String,
    val pnl: Double?,         // null = still open
    val notes: String = "",
    val lesson: String = "",
    val isOpen: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    // Position tracking (filled when open)
    val sellCE: Double = 0.0,
    val buyCE: Double = 0.0,
    val sellPE: Double = 0.0,
    val buyPE: Double = 0.0,
    val currentNifty: Double = 0.0,
)
