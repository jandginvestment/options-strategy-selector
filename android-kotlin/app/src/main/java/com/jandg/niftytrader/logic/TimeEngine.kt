package com.jandg.niftytrader.logic

import java.time.ZoneId
import java.time.ZonedDateTime

object TimeEngine {
    val IST: ZoneId = ZoneId.of("Asia/Kolkata")
    val DXB: ZoneId = ZoneId.of("Asia/Dubai")

    fun nowIst(): ZonedDateTime = ZonedDateTime.now(IST)
    fun nowDxb(): ZonedDateTime = ZonedDateTime.now(DXB)

    fun marketPhase(): MarketPhase {
        val ist = nowIst()
        val dow = ist.dayOfWeek.value // 1=Mon, 7=Sun
        if (dow >= 6) return MarketPhase.WEEKEND
        val mins = ist.hour * 60 + ist.minute
        return when {
            mins < 9 * 60 + 15  -> MarketPhase.PRE
            mins < 10 * 60 + 30 -> MarketPhase.WAIT
            mins < 14 * 60 + 30 -> MarketPhase.ENTRY
            mins < 15 * 60 + 30 -> MarketPhase.LATE
            else                 -> MarketPhase.CLOSED
        }
    }

    fun isThursday(): Boolean = nowIst().dayOfWeek.value == 4

    fun fmt12(hour: Int, minute: Int): String {
        val ap = if (hour >= 12) "PM" else "AM"
        val h  = if (hour % 12 == 0) 12 else hour % 12
        return "%d:%02d %s".format(h, minute, ap)
    }

    /** Minutes until a target IST time (returns 0 if past) */
    fun minsUntilIst(targetH: Int, targetM: Int): Int {
        val ist = nowIst()
        val cur = ist.hour * 60 + ist.minute
        val tgt = targetH * 60 + targetM
        return maxOf(0, tgt - cur)
    }
}

enum class MarketPhase(val label: String, val emoji: String) {
    WEEKEND("Weekend",        "🔴"),
    PRE    ("Pre-Market",     "⏳"),
    WAIT   ("Wait — Settling","⚡"),
    ENTRY  ("ENTRY WINDOW",   "✅"),
    LATE   ("Late Session",   "⚠️"),
    CLOSED ("Market Closed",  "🔴"),
}
