package com.example.vapeshop.domain.usecase.order

import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): List<Order> {
        return orderRepository.getOrders()
    }
}