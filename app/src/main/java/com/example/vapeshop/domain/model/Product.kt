package com.example.vapeshop.domain.model

data class Product(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,
    val categoryId: String? = null
)
