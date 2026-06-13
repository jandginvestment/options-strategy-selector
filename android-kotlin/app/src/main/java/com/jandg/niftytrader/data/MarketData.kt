package com.jandg.niftytrader.data

import com.google.gson.annotations.SerializedName

data class MarketData(
    @SerializedName("nifty")        val nifty: Double = 0.0,
    @SerializedName("vix")          val vix: Double = 0.0,
    @SerializedName("prevClose")    val prevClose: Double = 0.0,
    @SerializedName("gapPct")       val gapPct: Double = 0.0,
    @SerializedName("vixDirection") val vixDirection: String = "stable",
    @SerializedName("bestExpiry")   val bestExpiry: String = "",
    @SerializedName("daysToExpiry") val daysToExpiry: Int = 0,
    @SerializedName("expiryLabel")  val expiryLabel: String = "",
    @SerializedName("marketPhase")  val marketPhase: String = "closed",
    @SerializedName("timestamp")    val timestamp: String = "",
    @SerializedName("cached")       val cached: Boolean = false,
    @SerializedName("error")        val error: String? = null,
    @SerializedName("message")      val message: String? = null,
)
