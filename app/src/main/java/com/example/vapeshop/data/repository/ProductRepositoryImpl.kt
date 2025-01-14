package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.local.CartItemEntity
import com.example.vapeshop.domain.ProductRepository
import com.example.vapeshop.domain.model.CartResponse
import com.example.vapeshop.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun getProducts(categoryId: String): List<Product> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot =
                    firestore.collection("categories").document(categoryId).collection("products")
                        .get()
                        .await()
                snapshot.toObjects(Product::class.java)
            } catch (e: Exception) {
                Log.e("ProductRepositoryImpl", "Error fetching products", e)
                emptyList()
            }
        }

    override suspend fun getProductById(productId: String, categoryId: String): Product =
        withContext(Dispatchers.IO) {
            try {
                val snapshot =
                    firestore.collection("categories").document(categoryId).collection("products")
                        .document(productId).get().await()
                snapshot.toObject(Product::class.java) ?: throw Exception("Product not found")
            } catch (_: Exception) {
                Product()
            }
        }

    override suspend fun getCart(userId: String): List<CartItemEntity> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("carts").document(userId).get().await()
                snapshot.toObject(CartResponse::class.java)?.items ?: emptyList()
            } catch (e: Exception) {
                Log.e("ProductRepositoryImpl", "Error getting cart: ${e.message}")
                emptyList()
            }
        }

    override suspend fun updateCart(
        userId: String,
        cartItems: List<CartItemEntity>
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val cartData = CartResponse(userId = userId, items = cartItems)
            firestore.collection("carts")
                .document(userId)
                .set(cartData)
                .await()
        } catch (e: Exception) {
            Log.e("ProductRepositoryImpl", "Error updating cart: ${e.message}")
        }
    }
}