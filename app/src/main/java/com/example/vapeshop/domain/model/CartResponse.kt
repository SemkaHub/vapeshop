package com.example.vapeshop.domain.model

import com.example.vapeshop.data.local.CartItemEntity

data class CartResponse(val items: List<CartItemEntity> = emptyList())
