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

    private val _authUiState = MutableLiveData<AuthUiState>(AuthUiState.Initial)
    val authUiState: LiveData<AuthUiState>
        get() = _authUiState

    override fun onCleared() {
        super.onCleared()
        _authUiState.value = AuthUiState.Initial
    }

    fun loginUser(email: String, password: String) {
        if (!validate(email, password)) return
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
                            _authUiState.value =
                                AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
                        }
                    }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
            }
        }
    }

    fun registerUser(email: String, password: String) {
        if (!validate(email, password)) return
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
                            _authUiState.value =
                                AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
                        }
                    }
            } catch (_: Exception) {
                _authUiState.value = AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
            }
        }
    }

    private fun saveUser(user: User) = viewModelScope.launch {
        userRepository.saveUser(user)
    }

    private fun validate(email: String, password: String): Boolean {
        var isValid = true

        if (!isValidEmail(email)) {
            _authUiState.value = AuthUiState.Error(emailError = AuthErrorType.INVALID_EMAIL)
            isValid = false
        }

        if (!isValidPassword(password)) {
            if (!isValid) {
                _authUiState.value =
                    AuthUiState.Error(
                        passwordError = AuthErrorType.PASSWORD_SHORT,
                        emailError = AuthErrorType.INVALID_EMAIL
                    )
            } else {
                _authUiState.value =
                    AuthUiState.Error(passwordError = AuthErrorType.PASSWORD_SHORT)
                isValid = false
            }
        }
        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    sealed class AuthUiState {
        object Initial : AuthUiState()
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(
            val message: AuthErrorType? = null,
            val emailError: AuthErrorType? = null,
            val passwordError: AuthErrorType? = null
        ) : AuthUiState()
    }

    enum class AuthErrorType {
        INVALID_EMAIL,
        PASSWORD_SHORT,
        GENERIC_ERROR
    }
}
