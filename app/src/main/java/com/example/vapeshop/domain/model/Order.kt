package com.example.vapeshop.domain.model

data class Order(
    val items: List<CartItem>,
    val totalPrice: Double,
    val deliveryMethod: DeliveryMethod,
    val paymentMethod: PaymentMethod,
    val deliveryAddress: Address?,
    val pickupPointId: String?,
)

enum class DeliveryMethod { COURIER, PICKUP }
enum class PaymentMethod { ONLINE, ON_DELIVERY }