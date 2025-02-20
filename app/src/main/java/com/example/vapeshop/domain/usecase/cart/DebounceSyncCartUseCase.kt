package com.example.vapeshop.domain.usecase.cart

import com.example.vapeshop.domain.manager.SyncStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DebounceSyncCartUseCase @Inject constructor(
    private val syncCartUseCase: SyncCartUseCase,
    private val coroutineScope: CoroutineScope,
    private val syncStateManager: SyncStateManager,
) {

    private var debounceJob: Job? = null
    private val debounceDelayMillis = 1500L

    suspend operator fun invoke() {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            syncStateManager.setSyncState(true)
            delay(debounceDelayMillis)
            syncCartUseCase()
            syncStateManager.setSyncState(false)
        }
    }
}