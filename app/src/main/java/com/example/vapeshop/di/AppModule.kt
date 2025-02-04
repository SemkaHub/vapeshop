package com.example.vapeshop.di

import android.content.Context
import com.example.vapeshop.data.ResourceProviderImpl
import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.factory.CategoryAdapterFactory
import com.example.vapeshop.presentation.adapter.factory.ProductAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProviderImpl(context)

    @Provides
    @Singleton
    fun provideProductAdapterFactory(resourceProvider: ResourceProvider): ProductAdapterFactory =
        ProductAdapterFactory(resourceProvider)

    @Provides
    @Singleton
    fun provideCategoryAdapterFactory(resourceProvider: ResourceProvider): CategoryAdapterFactory =
        CategoryAdapterFactory(resourceProvider)
}