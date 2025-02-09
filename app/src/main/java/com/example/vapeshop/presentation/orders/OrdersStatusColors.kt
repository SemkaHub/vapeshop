package com.example.vapeshop.presentation.orders

data class OrdersStatusColors(
    val backgroundPending: Int,
    val backgroundProcessing: Int,
    val backgroundShipped: Int,
    val backgroundDelivered: Int,
    val backgroundCancelled: Int,
    val textPending: Int,
    val textProcessing: Int,
    val textShipped: Int,
    val textDelivered: Int,
    val textCancelled: Int
)
