package com.example.vapeshop.presentation.adapter.factory

import com.example.vapeshop.domain.factory.CategoryAdapterFactory
import com.example.vapeshop.domain.util.ResourceProvider
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CategoryAdapterFactoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : CategoryAdapterFactory {
    override fun create(
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