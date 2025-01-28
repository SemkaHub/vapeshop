package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.domain.repository.CategoryRepository
import com.example.vapeshop.domain.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            Log.d("CategoryRepositoryImpl", "Snapshot size: ${snapshot.documents.size}")
            snapshot.documents.map { document ->
                val category = Category(
                    id = document.id,
                    name = document.getString("name"),
                    imageUrl = document.getString("imageUrl")
                )
                Log.d("CategoryRepositoryImpl", "Category: $category")
                category
            }
        } catch (e: Exception) {
            Log.e("CategoryRepositoryImpl", "Error fetching categories", e)
            emptyList()
        }
    }
}