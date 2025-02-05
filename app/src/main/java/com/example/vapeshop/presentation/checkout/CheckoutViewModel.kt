package com.example.vapeshop.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.usecase.order.CalculateTotalPriceUseCase
import com.example.vapeshop.domain.usecase.order.CreateOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val calculateTotalPriceUseCase: CalculateTotalPriceUseCase
) : ViewModel() {

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            createOrderUseCase.invoke(order)
        }
    }

    fun calculateTotalPrice(items: List<CartItem>): Double =
        calculateTotalPriceUseCase.invoke(items)
}