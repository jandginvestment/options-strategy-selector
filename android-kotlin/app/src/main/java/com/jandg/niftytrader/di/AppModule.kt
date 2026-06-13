package com.jandg.niftytrader.di

import android.content.Context
import androidx.room.Room
import com.jandg.niftytrader.BuildConfig
import com.jandg.niftytrader.data.MarketApi
import com.jandg.niftytrader.data.TradeDao
import com.jandg.niftytrader.data.TradeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideMarketApi(retrofit: Retrofit): MarketApi =
        retrofit.create(MarketApi::class.java)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): TradeDatabase =
        Room.databaseBuilder(ctx, TradeDatabase::class.java, "niftytrader.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun provideTradeDao(db: TradeDatabase): TradeDao = db.tradeDao()
}
