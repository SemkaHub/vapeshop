package com.example.vapeshop.presentation.category

import com.example.vapeshop.domain.model.Category

sealed class CategoryState {
    object Loading : CategoryState()
    data class Content(val categories: List<Category>) : CategoryState()
    data class Error(val message: String, val retryAction: () -> Unit) : CategoryState()
}