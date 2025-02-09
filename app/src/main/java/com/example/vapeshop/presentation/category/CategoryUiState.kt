package com.example.vapeshop.presentation.category

import com.example.vapeshop.domain.model.Category

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    data class Content(val categories: List<Category>) : CategoryUiState()
    data class Error(val message: String, val retryAction: () -> Unit) : CategoryUiState()
}