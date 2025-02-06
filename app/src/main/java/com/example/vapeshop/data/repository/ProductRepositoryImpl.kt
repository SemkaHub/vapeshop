package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun getProducts(categoryId: String): List<Product> {
        return try {
            val snapshot =
                firestore.collection("categories").document(categoryId).collection("products")
                    .get()
                    .await()
            snapshot.map { item ->
                Product(
                    id = item.id,
                    name = item.getString("name") ?: "",
                    description = item.getString("description") ?: "",
                    price = item.getDouble("price") ?: 0.0,
                    imageUrl = item.getString("imageUrl") ?: "",
                    isAvailable = item.getBoolean("isAvailable") != false
                )
            }
        } catch (e: Exception) {
            Log.e("ProductRepositoryImpl", "Error fetching products", e)
            emptyList()
        }
    }

    override suspend fun getProductById(productId: String, categoryId: String): Product {
        return try {
            val snapshot =
                firestore.collection("categories").document(categoryId).collection("products")
                    .document(productId).get().await()
            snapshot.toObject(Product::class.java) ?: throw Exception("Product not found")
        } catch (_: Exception) {
            Product()
        }
    }
}