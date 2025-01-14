package com.example.vapeshop.domain

import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product

interface CartRepository {
    suspend fun addToCart(product: Product, quantity: Int = 1)
    suspend fun getCartItems(): List<CartItem>
    suspend fun increaseQuantity(productId: String)
    suspend fun decreaseQuantity(productId: String)
    suspend fun removeFromCart(productId: String)
}