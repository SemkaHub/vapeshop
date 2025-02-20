package com.example.vapeshop.data.remote

import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.domain.model.CartResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : RemoteDataSource {
    // Отправляем локальную корзину на сервер
    override suspend fun syncCartWithServer(cart: CartResponse) {
        val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
        firestore.collection("carts").document(userId).set(cart).await()
    }

    // Получаем корзину с сервера
    override suspend fun getCartFromServer(): List<CartItemEntity>? {
        val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
        val snapshot = firestore.collection("carts").document(userId).get().await()
        return snapshot.toObject(CartResponse::class.java)?.items
    }
}