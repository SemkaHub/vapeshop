package com.example.vapeshop.data.extensions

import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.domain.model.CartItem

fun CartItem.toCartItemEntity() = CartItemEntity(
    productId = product.id,
    quantity = quantity,
    price = product.price,
    imageUrl = product.imageUrl,
    name = product.name
)