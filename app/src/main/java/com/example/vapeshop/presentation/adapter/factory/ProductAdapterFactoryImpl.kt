package com.example.vapeshop.presentation.adapter.factory

import com.example.vapeshop.domain.factory.ProductAdapterFactory
import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.ProductAdapter
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ProductAdapterFactoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ProductAdapterFactory {

    override fun create(
        cardWidth: Int,
        onAddToCartClick: (String) -> Unit
    ): ProductAdapter {
        return ProductAdapter(
            cardWidth = cardWidth,
            resourceProvider = resourceProvider,
            onAddToCartClick = onAddToCartClick
        )
    }
}