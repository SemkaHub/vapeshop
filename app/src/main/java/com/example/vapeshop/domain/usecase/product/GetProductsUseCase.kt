package com.example.vapeshop.domain.usecase.product

import com.example.vapeshop.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(categoryId: String) = productRepository.getProducts(categoryId)
}