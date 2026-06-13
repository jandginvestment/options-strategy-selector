package com.jandg.niftytrader.logic

enum class Strategy { LONG_STRANGLE, IRON_CONDOR, BULL_PUT_SPREAD, SKIP }

object StrategyEngine {
    fun select(vix: Double): Strategy = when {
        vix < 16.0 -> Strategy.LONG_STRANGLE
        vix < 18.0 -> Strategy.IRON_CONDOR
        vix < 20.0 -> Strategy.BULL_PUT_SPREAD
        else       -> Strategy.SKIP
    }

    fun strategyName(s: Strategy) = when (s) {
        Strategy.LONG_STRANGLE   -> "Long Strangle"
        Strategy.IRON_CONDOR     -> "Iron Condor"
        Strategy.BULL_PUT_SPREAD -> "Bull Put Spread"
        Strategy.SKIP            -> "SKIP Income Trade"
    }

    fun strategyEmoji(s: Strategy) = when (s) {
        Strategy.LONG_STRANGLE   -> "↔️"
        Strategy.IRON_CONDOR     -> "🏗️"
        Strategy.BULL_PUT_SPREAD -> "🐂"
        Strategy.SKIP            -> "⛔"
    }

    fun ppSignal(vix: Double): PpSignal = when {
        vix < 14.0 -> PpSignal("🟢 BEST TIME — Buy now! Cheapest premiums.",   PpLevel.BEST)
        vix < 16.0 -> PpSignal("🟢 GOOD — Premiums reasonable. Buy now.",       PpLevel.GOOD)
        vix < 17.0 -> PpSignal("🟡 ACCEPTABLE — Slightly elevated. OK to buy.", PpLevel.OK)
        vix < 18.0 -> PpSignal("🟠 WAIT — Wait for VIX dip below 17.",          PpLevel.WAIT)
        vix < 20.0 -> PpSignal("🔴 TOO EXPENSIVE — Don't buy PP now.",          PpLevel.SKIP)
        else       -> PpSignal("🚨 URGENT — Buy PP immediately! Portfolio risk!", PpLevel.URGENT)
    }
}

data class PpSignal(val message: String, val level: PpLevel)
enum class PpLevel { BEST, GOOD, OK, WAIT, SKIP, URGENT }
