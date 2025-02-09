package com.example.vapeshop.presentation.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.usecase.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _products = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val products: StateFlow<ProductListUiState> = _products.asStateFlow()

    fun getProducts(categoryId: String) {
        viewModelScope.launch {
            try {
                _products.value = ProductListUiState.Loading
                val productList = getProductsUseCase(categoryId)
                if (productList.isEmpty()) {
                    _products.value = ProductListUiState.Empty
                } else {
                    _products.value = ProductListUiState.Content(productList)
                }
            } catch (e: Exception) {
                _products.value =
                    ProductListUiState.Error(e.message.toString()) { getProducts(categoryId) }
            }
        }
    }
}