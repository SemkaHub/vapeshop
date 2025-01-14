package com.example.vapeshop.domain.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val isAvailable: Boolean = true
)
