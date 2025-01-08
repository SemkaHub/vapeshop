package com.example.vapeshop.domain.factory

import com.example.vapeshop.presentation.adapter.ProductAdapter

interface ProductAdapterFactory {
    fun create(cardWidth: Int, onAddToCartClick: (String) -> Unit): ProductAdapter
}