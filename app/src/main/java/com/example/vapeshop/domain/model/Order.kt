package com.example.vapeshop.domain.model

data class Order(
    val items: List<CartItem>,
    val totalPrice: Double,
    val deliveryMethod: DeliveryMethod,
    val paymentMethod: PaymentMethod,
    val deliveryAddress: Address?,
    val pickupPointId: String?,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DeliveryMethod { COURIER, PICKUP }
enum class PaymentMethod { ONLINE, ON_DELIVERY }
enum class PaymentStatus { PENDING, PAID }