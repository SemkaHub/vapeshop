package com.example.vapeshop.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.usecase.user.GetCurrentUserUseCase
import com.example.vapeshop.domain.usecase.user.GetUserProfileUseCase
import com.example.vapeshop.domain.usecase.user.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private var currentJob: Job? = null

    fun getCurrentUser() {
        // Отменяем предыдущий запрос если он есть
        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            try {
                _profileUiState.emit(ProfileUiState.Loading)
                val user = getCurrentUserUseCase()
                val userProfile = getUserProfileUseCase()
                if (user != null) {
                    _profileUiState.emit(
                        ProfileUiState.Success(
                            user.copy(
                                name = userProfile?.firstName
                            )
                        )
                    )
                } else {
                    _profileUiState.emit(ProfileUiState.UnAuthorized)
                }
            } catch (e: Exception) {
                _profileUiState.emit(ProfileUiState.Error("Failed to fetch user: ${e.message}"))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _profileUiState.emit(ProfileUiState.Loading)
                signOutUseCase()
                _profileUiState.emit(ProfileUiState.UnAuthorized)
            } catch (e: Exception) {
                _profileUiState.emit(ProfileUiState.Error("Failed to sign out: ${e.message}"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentJob?.cancel()
    }

    sealed class ProfileUiState {
        object Loading : ProfileUiState()
        data class Success(val user: User) : ProfileUiState()
        data class Error(val message: String) : ProfileUiState()
        object UnAuthorized : ProfileUiState()
    }
}