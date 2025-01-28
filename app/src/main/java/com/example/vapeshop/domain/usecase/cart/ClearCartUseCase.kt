package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke() = cartRepository.clearCart()
}