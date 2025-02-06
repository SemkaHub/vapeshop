package com.example.vapeshop.data.repository

import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : OrderRepository {

    override suspend fun createOrder(order: Order) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

        val orderData = hashMapOf(
            "items" to order.items,
            "totalPrice" to order.totalPrice,
            "deliveryMethod" to order.deliveryMethod,
            "deliveryAddress" to order.deliveryAddress,
            "pickupPointId" to order.pickupPointId,
            "paymentMethod" to order.paymentMethod,
            "paymentStatus" to order.paymentStatus,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("orders")
            .document(uid)
            .collection("user_orders")
            .add(orderData)
            .await()
    }
}