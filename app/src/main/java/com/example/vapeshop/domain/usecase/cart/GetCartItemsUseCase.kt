package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.model.CartItem
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): List<CartItem> {
        return cartRepository.getCartItems()
    }
}