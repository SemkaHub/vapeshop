package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.model.Product
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int) {
        cartRepository.addToCart(product, quantity)
    }
}