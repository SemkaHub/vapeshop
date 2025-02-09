package com.example.vapeshop.presentation.orders

import com.example.vapeshop.domain.model.OrderStatus

data class OrdersAdapterStrings(
    val orderId: String,
    val orderDate: String,
    val orderStatus: Map<OrderStatus, String>,
    val orderTotalPrice: String,
    val orderDeliveryPickup: String,
    val orderDeliveryCourier: String
)
