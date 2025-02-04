package com.example.vapeshop.di

import android.content.Context
import androidx.room.Room
import com.example.vapeshop.data.local.AppDatabase
import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "vape_shop_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao = appDatabase.cartDao()
}