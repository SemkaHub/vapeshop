package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.data.extensions.toCartItemEntity
import com.example.vapeshop.data.remote.RemoteDataSource
import com.example.vapeshop.domain.model.CartResponse
import com.example.vapeshop.domain.repository.CartRepository
import javax.inject.Inject

class SyncCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val remoteDataSource: RemoteDataSource,
) {
    suspend operator fun invoke() {
        val localCart = cartRepository.getLocalCart().map { it.toCartItemEntity() }
        remoteDataSource.syncCartWithServer(CartResponse(items = localCart))
    }
}