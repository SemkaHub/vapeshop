package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemCountUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<Int> {
        return cartRepository.getCartItemCount()
    }
}