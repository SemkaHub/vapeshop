package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.Order

interface OrderRepository {
    suspend fun createOrder(order: Order)
    suspend fun getOrders(): List<Order>
}