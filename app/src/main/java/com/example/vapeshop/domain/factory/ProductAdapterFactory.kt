package com.example.vapeshop.domain.factory

import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.presentation.adapter.ProductAdapter

interface ProductAdapterFactory {
    fun create(cardWidth: Int, onAddToCartClick: (Product, Int) -> Unit): ProductAdapter
}