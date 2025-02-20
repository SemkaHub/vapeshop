package com.example.vapeshop.data.remote

import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.domain.model.CartResponse

interface RemoteDataSource {
    suspend fun syncCartWithServer(cart: CartResponse)
    suspend fun getCartFromServer(): List<CartItemEntity>?
}