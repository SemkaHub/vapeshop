package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.domain.ProductRepository
import com.example.vapeshop.domain.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    override suspend fun getProducts(categoryId: String): List<Product> {
        return try {
            val snapshot =
                firestore.collection("categories").document(categoryId).collection("products").get()
                    .await()
            snapshot.documents.map { document ->
                Product(
                    id = document.id,
                    name = document.getString("name"),
                    description = document.getString("description"),
                    price = document.getDouble("price"),
                    imageUrl = document.getString("imageUrl"),
                    isAvailable = document.getBoolean("isAvailable") != false
                )
            }
        } catch (e: Exception) {
            Log.e("Products", "Error fetching products", e)
            emptyList()
        }
    }
}