package com.example.vapeshop.presentation.map

import com.example.vapeshop.domain.model.Address

sealed class MapUiState {
    object Loading : MapUiState()
    object Success: MapUiState()
    data class Content(val address: Address) : MapUiState()
    data class Error(val error: MapAddressError) : MapUiState()
}

enum class MapAddressError {
    INCORRECT_ADDRESS,
    NO_APARTMENT,
    NO_INTERNET,
    UNKNOWN_ERROR
}