package com.example.vapeshop.domain.factory

import com.example.vapeshop.presentation.adapter.CategoryAdapter

interface CategoryAdapterFactory {
    fun create(cardWidth: Int, onClickListener: ((String) -> Unit)? = null): CategoryAdapter
}