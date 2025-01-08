package com.example.vapeshop.data.repository

import com.example.vapeshop.data.local.AppDatabase
import com.example.vapeshop.data.local.CartItemEntity
import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.ProductRepository
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val productRepository: ProductRepository
) : CartRepository {

    override suspend fun addItemToCart(productId: String) {
        database.cartDao().insertCartItem(CartItemEntity(productId = productId, quantity = 1))
    }
}