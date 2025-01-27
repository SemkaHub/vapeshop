package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartSyncStateUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return cartRepository.observeSyncState()
    }
}