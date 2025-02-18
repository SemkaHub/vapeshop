package com.example.vapeshop.presentation.profilesettings

import com.example.vapeshop.domain.model.UserProfile

sealed class ProfileSettingsUiState {
    object Initial : ProfileSettingsUiState()
    data class Content(val userProfile: UserProfile) : ProfileSettingsUiState()
    object Loading : ProfileSettingsUiState()
    object Success : ProfileSettingsUiState()
    data class Error(val message: String) : ProfileSettingsUiState()
}