package com.example.vapeshop.presentation.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.Category
import com.example.vapeshop.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val categories: StateFlow<CategoryUiState> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = CategoryUiState.Loading
            try {
                val categoriesList = categoryRepository.getCategories()
                if (categoriesList != emptyList<Category>()) {
                    _categories.value = CategoryUiState.Content(categoriesList)
                } else {
                    throw Exception("Categories list is empty")
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error loading categories: ${e.message}", e)
                _categories.value =
                    CategoryUiState.Error(
                        message = e.message.toString(),
                        retryAction = { loadCategories() })
            }
        }
    }
}