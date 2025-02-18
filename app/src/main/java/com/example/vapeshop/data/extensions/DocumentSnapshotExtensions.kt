package com.example.vapeshop.data.extensions

import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.DeliveryMethod
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.model.OrderStatus
import com.example.vapeshop.domain.model.PaymentMethod
import com.example.vapeshop.domain.model.PaymentStatus
import com.example.vapeshop.domain.model.Product
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toOrder(): Order? {
    val id = this.id
    val totalPrice = getDouble("totalPrice") ?: return null
    val pickupPointId = getString("pickupPointId") ?: ""
    val timestamp = getLong("timestamp") ?: return null

    val statusString = getString("status") ?: OrderStatus.PENDING.name
    val status = OrderStatus.valueOf(statusString)

    val deliveryMethodString =
        getString("deliveryMethod") ?: return null
    val deliveryMethod = try {
        DeliveryMethod.valueOf(deliveryMethodString)
    } catch (e: Exception) {
        return null
    }

    var deliveryAddress: Address?
    try {
        val deliveryAddressMap = get("deliveryAddress") as Map<*, *>
        deliveryAddress = Address(
            city = deliveryAddressMap["city"] as String,
            street = deliveryAddressMap["street"] as String,
            apartment = deliveryAddressMap["apartment"] as String
        )
    } catch (e: Exception) {
        deliveryAddress = null
    }


    val paymentMethodString = getString("paymentMethod") ?: ""
    val paymentMethod = try {
        PaymentMethod.valueOf(paymentMethodString)
    } catch (e: Exception) {
        PaymentMethod.ON_DELIVERY
    }

    val paymentStatusString = getString("paymentStatus") ?: ""
    val paymentStatus = try {
        PaymentStatus.valueOf(paymentStatusString)
    } catch (e: Exception) {
        PaymentStatus.PENDING
    }

    val itemsData = get("items") as? List<*> ?: return null
    val items = itemsData.mapNotNull { it.toCartItem() }

    return Order(
        id = id,
        status = status,
        items = items,
        totalPrice = totalPrice,
        deliveryMethod = deliveryMethod,
        paymentMethod = paymentMethod,
        deliveryAddress = deliveryAddress,
        pickupPointId = pickupPointId,
        paymentStatus = paymentStatus,
        timestamp = timestamp
    )
}

fun Any?.toCartItem(): CartItem? {
    val itemMap = this as? Map<*, *> ?: return null
    val productMap = itemMap["product"] as? Map<*, *> ?: return null
    val product = Product(
        id = productMap["id"] as String,
        name = productMap["name"] as String,
        price = productMap["price"] as Double,
        description = productMap["description"] as String,
        imageUrl = productMap["imageUrl"] as String,
        isAvailable = productMap["isAvailable"] as? Boolean != false
    )
    val quantity = itemMap["quantity"] as? Long ?: return null
    return CartItem(
        product = product,
        quantity = quantity.toInt()
    )
}