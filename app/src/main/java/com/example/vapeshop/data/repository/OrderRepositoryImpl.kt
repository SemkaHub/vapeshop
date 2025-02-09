package com.example.vapeshop.data.repository

import com.example.vapeshop.data.mapper.OrderMapper
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.repository.OrderRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val orderMapper: OrderMapper
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

    override suspend fun getOrders(): List<Order> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val snapshot = firestore.collection("orders")
                .document(uid)
                .collection("user_orders")
                .get()
                .await()

            snapshot.documents.mapNotNull(orderMapper::map)
        } catch (e: Exception) {
            throw e
        }
    }
}