package com.example.vapeshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    val quantity: Int,
    val price: Double,
    val imageUrl: String,
    val name: String
)
