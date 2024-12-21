package com.example.vapeshop.domain.model

data class User(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null
)
