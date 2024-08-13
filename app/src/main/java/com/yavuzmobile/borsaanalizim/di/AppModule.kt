package com.yavuzmobile.borsaanalizim.di

import android.content.Context
import androidx.room.Room
import com.yavuzmobile.borsaanalizim.data.local.AppDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.IsYatirimRepository
import com.yavuzmobile.borsaanalizim.data.api.FinTablesApi
import com.yavuzmobile.borsaanalizim.data.repository.remote.FinTablesRepository
import com.yavuzmobile.borsaanalizim.data.api.IsYatirimApi
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
            /*
            .addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + "")
                    .build()
                chain.proceed(request)
            })*/
            /*
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            */
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
    @Named("FinTables")
    fun provideFinTablesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.fintables.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(@Named("Default") retrofit: Retrofit): IsYatirimApi = retrofit.create(IsYatirimApi::class.java)

    @Provides
    @Singleton
    fun provideFinTablesApi(@Named("FinTables") retrofit: Retrofit): FinTablesApi = retrofit.create(
        FinTablesApi::class.java)

    @Provides
    @Singleton
    fun provideRemoteRepository(api: IsYatirimApi): IsYatirimRepository = IsYatirimRepository(api)

    @Provides
    @Singleton
    fun provideFinTablesRepository(api: FinTablesApi): FinTablesRepository = FinTablesRepository(api)

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
    fun provideLocalRepository(dao: BalanceSheetDateDao): LocalRepository = LocalRepository(dao)
}