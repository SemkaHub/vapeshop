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

    private val _categories = MutableStateFlow<CategoryState>(CategoryState.Loading)
    val categories: StateFlow<CategoryState> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = CategoryState.Loading
            try {
                val categoriesList = categoryRepository.getCategories()
                if (categoriesList != emptyList<Category>()) {
                    _categories.value = CategoryState.Content(categoriesList)
                } else {
                    throw Exception("Categories list is empty")
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error loading categories: ${e.message}", e)
                _categories.value =
                    CategoryState.Error(
                        message = e.message.toString(),
                        retryAction = { loadCategories() })
            }
        }
    }
}