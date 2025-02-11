package com.example.vapeshop.presentation.profilesettings

sealed class ProfileSettingsUiState {
    object Initial : ProfileSettingsUiState()
    object Loading : ProfileSettingsUiState()
    object Success : ProfileSettingsUiState()
    data class Error(val message: String) : ProfileSettingsUiState()
}