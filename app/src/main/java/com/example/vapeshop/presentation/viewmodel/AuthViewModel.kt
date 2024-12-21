package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.UserRepository
import com.example.vapeshop.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authUiState = MutableLiveData<AuthUiState>()
    val authUiState: LiveData<AuthUiState>
        get() = _authUiState

    fun loginUser(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseAuth.currentUser?.let {
                                saveUser(
                                    User(
                                        id = it.uid,
                                        name = it.displayName,
                                        email = it.email,
                                        phone = it.phoneNumber
                                    )
                                )
                            }
                            _authUiState.value = AuthUiState.Success
                        } else {
                            _authUiState.value = AuthUiState.Error(it.exception?.message ?: "Error")
                        }
                    }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun registerUser(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            firebaseAuth.currentUser?.let {
                                saveUser(
                                    User(
                                        id = it.uid,
                                        name = it.displayName,
                                        email = it.email,
                                        phone = it.phoneNumber
                                    )
                                )
                            }
                            _authUiState.value = AuthUiState.Success
                        } else {
                            _authUiState.value = AuthUiState.Error(it.exception?.message ?: "Error")
                        }
                    }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Error")
            }
        }
    }

    private fun saveUser(user: User) = viewModelScope.launch {
        userRepository.saveUser(user)
    }

    sealed class AuthUiState {
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val message: String) : AuthUiState()
    }
}
