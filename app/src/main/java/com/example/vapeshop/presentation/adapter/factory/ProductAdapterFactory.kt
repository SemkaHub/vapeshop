package com.example.vapeshop.presentation.adapter.factory

import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.ProductAdapter
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class ProductAdapterFactory @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun create(
        cardWidth: Int,
        onAddToCartClick: (Product, Int) -> Unit
    ): ProductAdapter {
        return ProductAdapter(
            cardWidth = cardWidth,
            resourceProvider = resourceProvider,
            onAddToCartClick = onAddToCartClick
        )
    }
}