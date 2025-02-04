package com.example.vapeshop.presentation.adapter.factory

import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CategoryAdapterFactory @Inject constructor(
    private val resourceProvider: ResourceProvider
) {
    fun create(
        cardWidth: Int,
        onClickListener: ((String) -> Unit)?
    ): CategoryAdapter {
        return CategoryAdapter(
            cardWidth = cardWidth,
            resourceProvider = resourceProvider,
            onItemClick = onClickListener
        )
    }
}