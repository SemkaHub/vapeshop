package com.example.vapeshop.domain.usecase.order

import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.repository.OrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(order: Order) = orderRepository.createOrder(order)
}