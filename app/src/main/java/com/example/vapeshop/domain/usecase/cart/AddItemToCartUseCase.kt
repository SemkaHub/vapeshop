package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.repository.CartRepository
import com.example.vapeshop.domain.model.Product
import javax.inject.Inject

class AddItemToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int) {
        cartRepository.addItemToCart(product, quantity)
    }
}