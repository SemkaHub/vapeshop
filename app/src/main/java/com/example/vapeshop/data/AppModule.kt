package com.example.vapeshop.data

import android.content.Context
import androidx.room.Room
import com.example.vapeshop.data.local.AppDatabase
import com.example.vapeshop.data.local.UserDao
import com.example.vapeshop.data.repository.CategoryRepositoryImpl
import com.example.vapeshop.data.repository.ProductRepositoryImpl
import com.example.vapeshop.data.repository.UserRepositoryImpl
import com.example.vapeshop.domain.CategoryRepository
import com.example.vapeshop.domain.ProductRepository
import com.example.vapeshop.domain.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth, userDao: UserDao): UserRepository =
        UserRepositoryImpl(firebaseAuth, userDao)

    @Provides
    @Singleton
    fun provideCategoryRepository(firestore: FirebaseFirestore): CategoryRepository =
        CategoryRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideProductRepository(firestore: FirebaseFirestore): ProductRepository =
        ProductRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext applicationContext: Context): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "vape_shop_database"
        ).build()
}