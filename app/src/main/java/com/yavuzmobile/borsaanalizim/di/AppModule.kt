package com.yavuzmobile.borsaanalizim.di

import android.content.Context
import androidx.room.Room
import com.yavuzmobile.borsaanalizim.data.api.Api
import com.yavuzmobile.borsaanalizim.data.api.BusinessInvestmentApi
import com.yavuzmobile.borsaanalizim.data.local.AppDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.BusinessInvestmentRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.StockMarketAnalysisRepository
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
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    @Named("Default")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.isyatirim.com.tr/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("BalanceSheetRatiosEntity")
    fun provideFinTablesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://borsaanalizim.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(@Named("Default") retrofit: Retrofit): BusinessInvestmentApi = retrofit.create(BusinessInvestmentApi::class.java)

    @Provides
    @Singleton
    fun provideFinTablesApi(@Named("BalanceSheetRatiosEntity") retrofit: Retrofit): Api = retrofit.create(
        Api::class.java)

    @Provides
    @Singleton
    fun provideRemoteRepository(api: BusinessInvestmentApi, balanceSheetDao: BalanceSheetDao): BusinessInvestmentRepository = BusinessInvestmentRepository(api, balanceSheetDao)

    @Provides
    @Singleton
    fun provideStockMarketAnalysisRepository(api: Api): StockMarketAnalysisRepository = StockMarketAnalysisRepository(api)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideStockDao(database: AppDatabase): StockDao = database.stockDao()

    @Provides
    @Singleton
    fun provideBalanceSheetDao(database: AppDatabase): BalanceSheetDao = database.financialStatementDao()

    @Provides
    @Singleton
    fun provideLocalRepository(stockDao: StockDao, balanceSheetDao: BalanceSheetDao): LocalRepository = LocalRepository(stockDao, balanceSheetDao)
}