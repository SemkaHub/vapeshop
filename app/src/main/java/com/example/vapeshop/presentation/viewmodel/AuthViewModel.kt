package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _authUiState = MutableLiveData<AuthUiState>()
    val authUiState: LiveData<AuthUiState>
        get() = _authUiState

    fun loginUser(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                // TODO: Implement login logic with Firebase
                _authUiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun registerUser(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                // TODO: Implement registration logic with Firebase
                _authUiState.value = AuthUiState.Success
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Error")
            }
        }
    }

    sealed class AuthUiState {
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }
}
