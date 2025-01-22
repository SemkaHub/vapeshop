package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.UserRepository
import com.example.vapeshop.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
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
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _profileUiState.emit(ProfileUiState.Success(user))
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
                userRepository.signOut()
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