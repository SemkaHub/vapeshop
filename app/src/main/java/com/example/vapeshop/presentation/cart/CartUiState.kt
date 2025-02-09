package com.example.vapeshop.presentation.cart

import com.example.vapeshop.domain.model.CartItem

sealed class CartUiState {
    object Loading : CartUiState()
    data class Content(val items: List<CartItem>, val totalPrice: Double) : CartUiState()
    object Empty : CartUiState()
    data class Error(val message: String, val retryAction: () -> Unit) : CartUiState()
}