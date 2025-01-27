package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.model.CartItem
import javax.inject.Inject

class CalculateCartTotalUseCase @Inject constructor() {
    operator fun invoke(cartItems: List<CartItem>): Double {
        return cartItems.fold(0.0) { acc, cartItem ->
            acc + (cartItem.product.price * cartItem.quantity)
        }
    }
}