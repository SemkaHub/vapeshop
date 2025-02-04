package com.example.vapeshop.domain.model

import com.example.vapeshop.data.local.entity.CartItemEntity

data class CartResponse(val items: List<CartItemEntity> = emptyList())
