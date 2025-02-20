package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.remote.RemoteDataSource
import javax.inject.Inject

class GetCartFromServerUseCase @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: CartDao,
) {
    suspend operator fun invoke() {
        val remoteCart = remoteDataSource.getCartFromServer()
        remoteCart?.map { item ->
            localDataSource.insertCartItem(item)
        }
    }
}