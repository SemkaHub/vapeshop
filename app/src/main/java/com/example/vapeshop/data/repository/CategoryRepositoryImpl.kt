package com.example.vapeshop.data.repository

import com.example.vapeshop.domain.CategoryRepository
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
            snapshot.documents.map { document ->
                Category(
                    id = document.id,
                    name = document.getString("name"),
                    imageUrl = document.getString("imageUrl")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}