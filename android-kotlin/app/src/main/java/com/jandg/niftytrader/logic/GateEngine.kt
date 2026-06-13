package com.jandg.niftytrader.logic

import kotlin.math.abs

data class Gate(
    val label: String,
    val value: String,
    val status: GateStatus,
)

enum class GateStatus { PASS, WARN, FAIL }

object GateEngine {
    fun run(
        phase: MarketPhase,
        isThursday: Boolean,
        gapPct: Double,
        daysToExpiry: Int,
        vixDirection: String,
    ): List<Gate> = listOf(
        timeGate(phase),
        gapGate(gapPct),
        expiryGate(daysToExpiry, isThursday),
        vixDirGate(vixDirection),
    )

    private fun timeGate(phase: MarketPhase) = when (phase) {
        MarketPhase.ENTRY   -> Gate("TIME", "Entry window open ✅ (9:00–11:00 AM Dubai)", GateStatus.PASS)
        MarketPhase.WAIT    -> Gate("TIME", "Too early — wait until 9:00 AM Dubai (10:30 IST)", GateStatus.WARN)
        MarketPhase.LATE    -> Gate("TIME", "Late session — reduced theta, consider skip", GateStatus.WARN)
        MarketPhase.PRE     -> Gate("TIME", "Pre-market — NSE opens at 7:45 AM Dubai", GateStatus.WARN)
        MarketPhase.WEEKEND -> Gate("TIME", "Weekend — market closed", GateStatus.FAIL)
        MarketPhase.CLOSED  -> Gate("TIME", "Market closed — NSE closes 2:00 PM Dubai", GateStatus.FAIL)
    }

    private fun gapGate(gapPct: Double): Gate {
        val absGap = abs(gapPct)
        val sign   = if (gapPct >= 0) "+" else ""
        return when {
            gapPct == 0.0 -> Gate("GAP",  "No prev close — enter it to check gap risk", GateStatus.WARN)
            absGap > 1.0  -> Gate("GAP",  "Gap ${sign}${"%.2f".format(gapPct)}% > 1.0% — HIGH RISK, consider skip", GateStatus.FAIL)
            absGap > 0.5  -> Gate("GAP",  "Gap ${sign}${"%.2f".format(gapPct)}% > 0.5% — CAUTION, wait for settle", GateStatus.WARN)
            else          -> Gate("GAP",  "Gap ${sign}${"%.2f".format(gapPct)}% < 0.5% — Clear ✅", GateStatus.PASS)
        }
    }

    private fun expiryGate(days: Int, isThursday: Boolean) = when {
        isThursday   -> Gate("EXPIRY", "TODAY is Thursday — never trade on expiry day!", GateStatus.FAIL)
        days <= 0    -> Gate("EXPIRY", "No expiry selected — pick a date", GateStatus.FAIL)
        days < 6     -> Gate("EXPIRY", "Only $days days to expiry — too close, pick next week", GateStatus.FAIL)
        days in 6..14 -> Gate("EXPIRY", "$days days to expiry ✅ — ideal window (8–14 days)", GateStatus.PASS)
        else         -> Gate("EXPIRY", "$days days to expiry — acceptable, prefer 8–14 days", GateStatus.WARN)
    }

    private fun vixDirGate(dir: String) = when (dir) {
        "falling"      -> Gate("VIX DIR", "VIX falling ↓ — premiums compressing, good for income ✅", GateStatus.PASS)
        "stable"       -> Gate("VIX DIR", "VIX stable — proceed normally ✅", GateStatus.PASS)
        "rising"       -> Gate("VIX DIR", "VIX rising ↑ — be cautious, consider waiting", GateStatus.WARN)
        "rising_sharp" -> Gate("VIX DIR", "VIX spiking ↑↑ — WAIT until VIX stabilises!", GateStatus.FAIL)
        else           -> Gate("VIX DIR", "VIX direction unknown — auto-detects after 30 min", GateStatus.WARN)
    }

    fun verdict(gates: List<Gate>): TradeVerdict {
        val hasHardFail = gates.any { it.status == GateStatus.FAIL }
        val hasWarn     = gates.any { it.status == GateStatus.WARN }
        return when {
            hasHardFail -> TradeVerdict.NO_GO
            hasWarn     -> TradeVerdict.CAUTION
            else        -> TradeVerdict.GO
        }
    }
}

enum class TradeVerdict { GO, CAUTION, NO_GO }
