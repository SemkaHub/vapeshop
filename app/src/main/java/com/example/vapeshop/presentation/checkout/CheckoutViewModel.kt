package com.example.vapeshop.presentation.checkout

import androidx.lifecycle.ViewModel
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.usecase.order.CalculateTotalPriceUseCase
import com.example.vapeshop.domain.usecase.order.CreateOrderUseCase
import com.example.vapeshop.domain.usecase.user.GetUserAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createOrderUseCase: CreateOrderUseCase,
    private val calculateTotalPriceUseCase: CalculateTotalPriceUseCase,
    private val getUserAddressUseCase: GetUserAddressUseCase,
) : ViewModel() {

    suspend fun placeOrder(order: Order) {
        createOrderUseCase.invoke(order)
    }

    fun calculateTotalPrice(items: List<CartItem>): Double =
        calculateTotalPriceUseCase.invoke(items)

    suspend fun getUserAddress() = getUserAddressUseCase()
}