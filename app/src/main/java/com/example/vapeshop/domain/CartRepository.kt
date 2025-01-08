package com.example.vapeshop.domain

interface CartRepository {
    suspend fun addItemToCart(productId: String)
}