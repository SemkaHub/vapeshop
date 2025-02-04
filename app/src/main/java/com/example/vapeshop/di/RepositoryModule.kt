package com.example.vapeshop.di

import com.example.vapeshop.data.repository.AuthRepositoryImpl
import com.example.vapeshop.data.repository.CartRepositoryImpl
import com.example.vapeshop.data.repository.CategoryRepositoryImpl
import com.example.vapeshop.data.repository.OrderRepositoryImpl
import com.example.vapeshop.data.repository.ProductRepositoryImpl
import com.example.vapeshop.data.repository.UserRepositoryImpl
import com.example.vapeshop.domain.repository.AuthRepository
import com.example.vapeshop.domain.repository.CartRepository
import com.example.vapeshop.domain.repository.CategoryRepository
import com.example.vapeshop.domain.repository.OrderRepository
import com.example.vapeshop.domain.repository.ProductRepository
import com.example.vapeshop.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
}