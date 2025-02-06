package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(categoryId: String): List<Product>
    suspend fun getProductById(productId: String, categoryId: String): Product
}