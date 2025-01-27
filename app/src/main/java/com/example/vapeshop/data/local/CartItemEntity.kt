package com.example.vapeshop.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey
    val productId: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val name: String = ""
)
