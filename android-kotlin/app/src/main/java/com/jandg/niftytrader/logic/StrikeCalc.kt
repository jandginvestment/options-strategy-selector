package com.jandg.niftytrader.logic

import kotlin.math.roundToInt

data class Leg(
    val action: String,   // "BUY" or "SELL"
    val strike: Int,
    val optType: String,  // "CE" or "PE"
    val estPrice: Int,
    val desc: String,
)

data class TradeCalc(
    val strategy: Strategy,
    val legs: List<Leg>,
    val netCredit: Int = 0,    // IC / BPS
    val totalDebit: Int = 0,   // Long Strangle
    val creditPerLot: Int = 0,
    val debitPerLot: Int = 0,
    val maxProfit: Int = 0,
    val target50: Int = 0,
    val stopLoss: Int = 0,
    val lowerBE: Int = 0,
    val upperBE: Int = 0,
    val lowerBEpct: Double = 0.0,
    val upperBEpct: Double = 0.0,
    val rangeLabel: String = "",
    val warning: String = "",
    val sensibullNote: String = "",
    val expiryLabel: String = "",
    val daysToExpiry: Int = 0,
)

object StrikeCalc {
    private fun round50(n: Double): Int = (n / 50).roundToInt() * 50

    fun calculate(
        strategy: Strategy,
        nifty: Double,
        vix: Double,
        lotSize: Int,
        expiry: String,
        expiryLabel: String,
        daysToExpiry: Int,
        wingWidth: Int = 500,
    ): TradeCalc? {
        return when (strategy) {
            Strategy.IRON_CONDOR     -> ironCondor(nifty, vix, lotSize, expiryLabel, daysToExpiry, wingWidth)
            Strategy.LONG_STRANGLE   -> longStrangle(nifty, vix, lotSize, expiryLabel, daysToExpiry)
            Strategy.BULL_PUT_SPREAD -> bullPutSpread(nifty, vix, lotSize, expiryLabel, daysToExpiry)
            Strategy.SKIP            -> null
        }
    }

    private fun ironCondor(nifty: Double, vix: Double, lotSize: Int, expiryLabel: String, days: Int, wing: Int): TradeCalc {
        val otmPct  = if (vix < 17.0) 0.018 else 0.020
        val sellPE  = round50(nifty * (1 - otmPct))
        val buyPE   = sellPE - wing
        val sellCE  = round50(nifty * (1 + otmPct))
        val buyCE   = sellCE + wing

        val sellPEprice = (nifty * 0.0045).roundToInt()
        val buyPEprice  = (sellPEprice * 0.25).roundToInt()
        val sellCEprice = (nifty * 0.0045).roundToInt()
        val buyCEprice  = (sellCEprice * 0.25).roundToInt()

        val netCredit   = (sellPEprice - buyPEprice) + (sellCEprice - buyCEprice)
        val creditPerLot = netCredit * lotSize
        val stopLoss    = creditPerLot * 2
        val target50    = creditPerLot / 2

        val lowerBE = sellPE - netCredit
        val upperBE = sellCE + netCredit
        val lowerPct = (nifty - lowerBE) / nifty * 100
        val upperPct = (upperBE - nifty) / nifty * 100

        val otmStr = "${"%.1f".format(otmPct * 100)}%"

        return TradeCalc(
            strategy   = Strategy.IRON_CONDOR,
            legs       = listOf(
                Leg("BUY",  buyCE,  "CE", buyCEprice,  "Upper wing — caps loss"),
                Leg("SELL", sellCE, "CE", sellCEprice, "Upper short — OTM $otmStr"),
                Leg("SELL", sellPE, "PE", sellPEprice, "Lower short — OTM $otmStr"),
                Leg("BUY",  buyPE,  "PE", buyPEprice,  "Lower wing — caps loss"),
            ),
            netCredit   = netCredit,
            creditPerLot = creditPerLot,
            maxProfit   = creditPerLot,
            target50    = target50,
            stopLoss    = stopLoss,
            lowerBE     = lowerBE,
            upperBE     = upperBE,
            lowerBEpct  = lowerPct,
            upperBEpct  = upperPct,
            rangeLabel  = "$sellPE – $sellCE",
            sensibullNote = "Payoff must show 'GET ₹X' (credit received) ✅",
            expiryLabel = expiryLabel,
            daysToExpiry = days,
        )
    }

    private fun longStrangle(nifty: Double, vix: Double, lotSize: Int, expiryLabel: String, days: Int): TradeCalc {
        val buyPE = round50(nifty * 0.985)
        val buyCE = round50(nifty * 1.015)

        val pePrice = (nifty * 0.0052).roundToInt()
        val cePrice = (nifty * 0.0052).roundToInt()
        val totalDebit  = pePrice + cePrice
        val debitPerLot = totalDebit * lotSize

        val lowerBE = buyPE - totalDebit
        val upperBE = buyCE + totalDebit
        val lowerPct = (nifty - lowerBE) / nifty * 100
        val upperPct = (upperBE - nifty) / nifty * 100

        val warning = when {
            vix > 17 -> "⚠️ VIX > 17 — premiums too expensive! Skip."
            pePrice > 150 || cePrice > 150 -> "⚠️ Est. premium > ₹150 — go further OTM!"
            else -> "✅ Both legs within ₹150 limit"
        }

        return TradeCalc(
            strategy  = Strategy.LONG_STRANGLE,
            legs      = listOf(
                Leg("BUY", buyPE, "PE", pePrice, "${"%.1f".format((nifty - buyPE) / nifty * 100)}% below Nifty"),
                Leg("BUY", buyCE, "CE", cePrice, "${"%.1f".format((buyCE - nifty) / nifty * 100)}% above Nifty"),
            ),
            totalDebit  = totalDebit,
            debitPerLot = debitPerLot,
            stopLoss    = debitPerLot,      // 100% of premium = max loss
            lowerBE     = lowerBE,
            upperBE     = upperBE,
            lowerBEpct  = lowerPct,
            upperBEpct  = upperPct,
            warning     = warning,
            sensibullNote = "Shows 'PAY ₹X' — correct for long strangle (you pay premium)",
            expiryLabel = expiryLabel,
            daysToExpiry = days,
        )
    }

    private fun bullPutSpread(nifty: Double, vix: Double, lotSize: Int, expiryLabel: String, days: Int): TradeCalc {
        val sellPE = round50(nifty * 0.975)
        val buyPE  = sellPE - 300

        val sellPrice = (nifty * 0.006).roundToInt()
        val buyPrice  = (sellPrice * 0.35).roundToInt()
        val netCredit = sellPrice - buyPrice
        val creditPerLot = netCredit * lotSize
        val maxLoss   = (300 - netCredit) * lotSize
        val target50  = creditPerLot / 2

        val lowerBE  = sellPE - netCredit
        val lowerPct = (nifty - lowerBE) / nifty * 100

        val warning = if (netCredit < 40)
            "⚠️ Net credit < ₹40 — skip this trade"
        else "✅ Net credit acceptable"

        return TradeCalc(
            strategy     = Strategy.BULL_PUT_SPREAD,
            legs         = listOf(
                Leg("SELL", sellPE, "PE", sellPrice, "Short put — OTM ${"%.1f".format((nifty - sellPE) / nifty * 100)}%"),
                Leg("BUY",  buyPE,  "PE", buyPrice,  "Long put — hedge"),
            ),
            netCredit    = netCredit,
            creditPerLot = creditPerLot,
            maxProfit    = creditPerLot,
            target50     = target50,
            stopLoss     = maxLoss,
            lowerBE      = lowerBE,
            lowerBEpct   = lowerPct,
            warning      = warning,
            sensibullNote = "Shows 'GET ₹X' — net credit received ✅",
            expiryLabel  = expiryLabel,
            daysToExpiry = days,
        )
    }
}
