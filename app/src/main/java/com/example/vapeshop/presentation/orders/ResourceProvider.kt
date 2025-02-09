package com.example.vapeshop.presentation.orders

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.vapeshop.R
import com.example.vapeshop.domain.model.OrderStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getOrdersAdapterStrings(): OrdersAdapterStrings {
        return OrdersAdapterStrings(
            orderId = context.getString(R.string.order_id),
            orderDate = context.getString(R.string.order_date),
            orderStatus = mapOf(
                OrderStatus.PENDING to context.getString(R.string.pending_order_status),
                OrderStatus.PROCESSING to context.getString(R.string.processing_order_status),
                OrderStatus.SHIPPED to context.getString(R.string.shipped_order_status),
                OrderStatus.DELIVERED to context.getString(R.string.delivered_order_status),
                OrderStatus.CANCELLED to context.getString(R.string.cancelled_order_status)
            ),
            orderTotalPrice = context.getString(R.string.order_total_price),
            orderDeliveryPickup = context.getString(R.string.order_pickup),
            orderDeliveryCourier = context.getString(R.string.order_delivery)
        )
    }

    fun getOrdersAdapterColors(): OrdersStatusColors {
        return OrdersStatusColors(
            backgroundPending = ContextCompat.getColor(context, R.color.background_grey),
            backgroundProcessing = ContextCompat.getColor(context, R.color.background_grey),
            backgroundShipped = ContextCompat.getColor(context, R.color.background_grey),
            backgroundDelivered = ContextCompat.getColor(context, R.color.background_green),
            backgroundCancelled = ContextCompat.getColor(context, R.color.background_red),
            textPending = ContextCompat.getColor(context, R.color.dark_grey),
            textProcessing = ContextCompat.getColor(context, R.color.dark_grey),
            textShipped = ContextCompat.getColor(context, R.color.dark_grey),
            textDelivered = ContextCompat.getColor(context, R.color.dark_green),
            textCancelled = ContextCompat.getColor(context, R.color.dark_red)
        )
    }
}