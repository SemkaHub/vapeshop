package com.example.vapeshop.domain.usecase.order

import com.example.vapeshop.domain.model.CartItem
import javax.inject.Inject

class CalculateTotalPriceUseCase @Inject constructor() {
    operator fun invoke(items: List<CartItem>): Double =
        items.sumOf { it.product.price * it.quantity }
}