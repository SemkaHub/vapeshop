package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.repository.CartRepository
import com.example.vapeshop.domain.model.CartItem
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(productId: String, increase: Boolean): List<CartItem> {
        return if (increase) {
            cartRepository.increaseQuantity(productId)
        } else {
            cartRepository.decreaseQuantity(productId)
        }
    }
}