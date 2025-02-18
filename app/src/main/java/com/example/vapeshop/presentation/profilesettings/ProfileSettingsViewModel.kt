package com.example.vapeshop.presentation.profilesettings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.domain.usecase.user.GetUserProfileFromLocalUseCase
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
    private val getUserProfileFromLocalUseCase: GetUserProfileFromLocalUseCase,
) : ViewModel() {

    private val _profileSettingsUiState =
        MutableStateFlow<ProfileSettingsUiState>(ProfileSettingsUiState.Initial)
    val profileSettingsUiState: StateFlow<ProfileSettingsUiState> =
        _profileSettingsUiState.asStateFlow()

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            try {
                _profileSettingsUiState.value = ProfileSettingsUiState.Loading
                val userProfile = getUserProfileFromLocalUseCase()
                if (userProfile != null)
                    _profileSettingsUiState.value = ProfileSettingsUiState.Content(userProfile)
            } catch (e: Exception) {
                Log.d("ProfileSettingsViewModel", "getUserProfile: ${e.message}")
            }
        }
    }

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