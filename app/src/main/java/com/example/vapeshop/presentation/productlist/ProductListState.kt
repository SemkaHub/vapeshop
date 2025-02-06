package com.example.vapeshop.presentation.productlist

import com.example.vapeshop.domain.model.Product

sealed class ProductListState {
    object Loading : ProductListState()
    data class Content(val products: List<Product>) : ProductListState()
    object Empty : ProductListState()
    data class Error(val message: String, val retryAction: () -> Unit) : ProductListState()
}