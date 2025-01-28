package com.example.vapeshop.di

import android.content.Context
import androidx.room.Room
import com.example.vapeshop.data.ResourceProviderImpl
import com.example.vapeshop.data.local.AppDatabase
import com.example.vapeshop.data.local.CartDao
import com.example.vapeshop.data.local.UserDao
import com.example.vapeshop.data.repository.CartRepositoryImpl
import com.example.vapeshop.data.repository.CategoryRepositoryImpl
import com.example.vapeshop.data.repository.OrderRepositoryImpl
import com.example.vapeshop.data.repository.ProductRepositoryImpl
import com.example.vapeshop.data.repository.UserRepositoryImpl
import com.example.vapeshop.domain.factory.CategoryAdapterFactory
import com.example.vapeshop.domain.factory.ProductAdapterFactory
import com.example.vapeshop.domain.repository.CartRepository
import com.example.vapeshop.domain.repository.CategoryRepository
import com.example.vapeshop.domain.repository.OrderRepository
import com.example.vapeshop.domain.repository.ProductRepository
import com.example.vapeshop.domain.repository.UserRepository
import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.factory.CategoryAdapterFactoryImpl
import com.example.vapeshop.presentation.adapter.factory.ProductAdapterFactoryImpl
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
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProviderImpl(context)

    @Provides
    @Singleton
    fun provideProductAdapterFactory(resourceProvider: ResourceProvider): ProductAdapterFactory =
        ProductAdapterFactoryImpl(resourceProvider)

    @Provides
    @Singleton
    fun provideCategoryAdapterFactory(resourceProvider: ResourceProvider): CategoryAdapterFactory =
        CategoryAdapterFactoryImpl(resourceProvider)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        userDao: UserDao,
        cartDao: CartDao
    ): UserRepository =
        UserRepositoryImpl(firebaseAuth, userDao, cartDao)

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
    fun provideCartRepository(
        localDataSource: CartDao,
        productRepository: ProductRepository,
        firebaseAuth: FirebaseAuth
    ): CartRepository = CartRepositoryImpl(localDataSource, productRepository, firebaseAuth)

    @Provides
    @Singleton
    fun provideOrderRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): OrderRepository =
        OrderRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideCartDao(appDatabase: AppDatabase): CartDao = appDatabase.cartDao()

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
}