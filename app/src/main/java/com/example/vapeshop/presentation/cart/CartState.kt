package com.example.vapeshop.presentation.cart

import com.example.vapeshop.domain.model.CartItem

sealed class CartState {
    object Loading : CartState()
    data class Content(val items: List<CartItem>, val totalPrice: Double) : CartState()
    object Empty : CartState()
    data class Error(val message: String, val retryAction: () -> Unit) : CartState()
}