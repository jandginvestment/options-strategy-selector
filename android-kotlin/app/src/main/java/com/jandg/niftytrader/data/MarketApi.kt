package com.jandg.niftytrader.data

import retrofit2.http.GET

interface MarketApi {
    @GET("market")
    suspend fun getMarket(): MarketData

    @GET("health")
    suspend fun health(): Map<String, String>
}
