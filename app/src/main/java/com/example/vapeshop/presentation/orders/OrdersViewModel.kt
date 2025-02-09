package com.example.vapeshop.presentation.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.usecase.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<OrdersUiState>(OrdersUiState.Loading)
    val state = _state.asStateFlow()

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            try {
                _state.value = OrdersUiState.Loading
                val orders = getOrdersUseCase()
                Log.d("OrdersViewModel", "Orders: $orders")
                if (orders.isEmpty()) {
                    _state.value = OrdersUiState.Empty
                } else {
                    _state.value = OrdersUiState.Content(orders)
                }
            } catch (e: Exception) {
                _state.value = OrdersUiState.Error(e.message ?: "Unknown error") { getOrders() }
                Log.d("OrdersViewModel", "Error getting orders: ${e.message}")
            }
        }
    }
}