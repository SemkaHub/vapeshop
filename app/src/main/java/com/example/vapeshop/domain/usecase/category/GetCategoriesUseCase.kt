package com.example.vapeshop.domain.usecase.category

import com.example.vapeshop.domain.model.Category
import com.example.vapeshop.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): List<Category> {
        return categoryRepository.getCategories()
    }
}