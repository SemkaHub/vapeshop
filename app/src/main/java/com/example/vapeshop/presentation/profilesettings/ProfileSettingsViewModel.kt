package com.example.vapeshop.presentation.profilesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.domain.usecase.user.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    private val _profileSettingsUiState =
        MutableStateFlow<ProfileSettingsUiState>(ProfileSettingsUiState.Initial)
    val profileSettingsUiState: StateFlow<ProfileSettingsUiState> =
        _profileSettingsUiState.asStateFlow()

    fun updateProfile(userProfile: UserProfile) {
        try {
            _profileSettingsUiState.value = ProfileSettingsUiState.Loading
            viewModelScope.launch {
                updateUserProfileUseCase(userProfile)
                _profileSettingsUiState.value = ProfileSettingsUiState.Success
            }
        } catch (e: Exception) {
            _profileSettingsUiState.value =
                ProfileSettingsUiState.Error(e.message ?: "Unknown error")
        }
    }
}