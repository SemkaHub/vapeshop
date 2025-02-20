package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addItemToCart(product: Product, quantity: Int = 1)
    suspend fun getLocalCart(): List<CartItem>
    suspend fun increaseQuantity(productId: String): List<CartItem>
    suspend fun decreaseQuantity(productId: String): List<CartItem>
    suspend fun removeFromCart(productId: String): List<CartItem>
    fun getCartItemCount(): Flow<Int>
    suspend fun clearCart()
}