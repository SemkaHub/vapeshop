package com.example.vapeshop.presentation.orders

import com.example.vapeshop.domain.model.Order

sealed class OrdersUiState {
    object Loading : OrdersUiState()
    object Empty : OrdersUiState()
    data class Content(val orders: List<Order>) : OrdersUiState()
    data class Error(val message: String, val retryAction: () -> Unit) : OrdersUiState()
}