package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(): List<CartItem> {
        return cartRepository.getLocalCart()
    }
}