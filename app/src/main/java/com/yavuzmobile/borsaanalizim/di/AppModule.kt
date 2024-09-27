package com.yavuzmobile.borsaanalizim.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.yavuzmobile.borsaanalizim.data.api.Api
import com.yavuzmobile.borsaanalizim.data.local.AppDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.dao.IndexDao
import com.yavuzmobile.borsaanalizim.data.local.dao.SectorDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
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

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://borsaanalizim.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

    @Provides
    @Singleton
    fun provideRemoteRepository(api: Api): RemoteRepository = RemoteRepository(api)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBalanceSheetDateDao(database: AppDatabase): BalanceSheetDateDao = database.balanceSheetDateDao()

    @Provides
    @Singleton
    fun provideBalanceSheetDao(database: AppDatabase): BalanceSheetDao = database.balanceSheetDao()

    @Provides
    @Singleton
    fun provideIndexDao(database: AppDatabase): IndexDao = database.indexDao()

    @Provides
    @Singleton
    fun provideSectorDao(database: AppDatabase): SectorDao = database.sectorDao()

    @Provides
    @Singleton
    fun provideStockDao(database: AppDatabase): StockDao = database.stockDao()

    @Provides
    @Singleton
    fun provideLocalRepository(
        balanceSheetDateDao: BalanceSheetDateDao,
        balanceSheetDao: BalanceSheetDao,
        stockDao: StockDao,
        sectorDao: SectorDao,
        indexDao: IndexDao
    ): LocalRepository = LocalRepository(balanceSheetDateDao, balanceSheetDao, stockDao, sectorDao, indexDao)
}