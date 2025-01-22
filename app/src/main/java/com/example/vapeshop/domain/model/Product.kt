package com.example.vapeshop.domain.model

data class Product(
    var id: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var isAvailable: Boolean = true
)
