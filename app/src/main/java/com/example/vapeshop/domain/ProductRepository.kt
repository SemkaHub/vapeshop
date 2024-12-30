package com.example.vapeshop.domain

import com.example.vapeshop.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(categoryId: String): List<Product>
}