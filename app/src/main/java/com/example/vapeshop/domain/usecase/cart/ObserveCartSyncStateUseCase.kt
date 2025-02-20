package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.manager.SyncStateManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartSyncStateUseCase @Inject constructor(
    private val syncStateManager: SyncStateManager,
) {
    operator fun invoke(): Flow<Boolean> {
        return syncStateManager.syncState
    }
}