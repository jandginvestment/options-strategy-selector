package com.jandg.niftytrader.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY createdAt DESC")
    fun allTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trades WHERE isOpen = 1 LIMIT 1")
    fun openPosition(): Flow<TradeEntity?>

    @Query("SELECT * FROM trades WHERE isOpen = 0 ORDER BY createdAt DESC")
    fun closedTrades(): Flow<List<TradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(trade: TradeEntity): Long

    @Update
    suspend fun update(trade: TradeEntity)

    @Delete
    suspend fun delete(trade: TradeEntity)

    @Query("SELECT COUNT(*) FROM trades WHERE isOpen = 0")
    suspend fun totalTrades(): Int

    @Query("SELECT SUM(pnl) FROM trades WHERE isOpen = 0 AND pnl IS NOT NULL")
    suspend fun totalPnl(): Double?

    @Query("SELECT COUNT(*) FROM trades WHERE isOpen = 0 AND pnl > 0")
    suspend fun winCount(): Int
}
