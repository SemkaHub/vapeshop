package com.example.vapeshop.domain

import com.example.vapeshop.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}