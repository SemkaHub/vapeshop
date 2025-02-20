package com.example.vapeshop.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStateManager @Inject constructor() {

    private val _syncState = MutableStateFlow(false)
    val syncState: StateFlow<Boolean> = _syncState.asStateFlow()

    fun setSyncState(state: Boolean) {
        _syncState.value = state
    }
}