package com.jandg.niftytrader.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TradeEntity::class], version = 1, exportSchema = false)
abstract class TradeDatabase : RoomDatabase() {
    abstract fun tradeDao(): TradeDao
}
