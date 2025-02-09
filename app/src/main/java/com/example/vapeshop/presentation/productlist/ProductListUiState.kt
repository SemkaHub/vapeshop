package com.example.vapeshop.presentation.productlist

import com.example.vapeshop.domain.model.Product

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Content(val products: List<Product>) : ProductListUiState()
    object Empty : ProductListUiState()
    data class Error(val message: String, val retryAction: () -> Unit) : ProductListUiState()
}