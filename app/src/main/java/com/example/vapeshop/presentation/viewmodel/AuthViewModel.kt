package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.repository.UserRepository
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.usecase.validation.ValidateEmailUseCase
import com.example.vapeshop.domain.usecase.validation.ValidatePasswordUseCase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {

    private val _authUiState = MutableLiveData<AuthUiState>(AuthUiState.Initial)
    val authUiState: LiveData<AuthUiState> = _authUiState

    override fun onCleared() {
        super.onCleared()
        _authUiState.value = AuthUiState.Initial
    }

    fun loginUser(email: String, password: String) {
        if (!validate(email, password)) return
        setLoadingState()

        viewModelScope.launch {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    saveUser(
                        User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName,
                            email = firebaseUser.email,
                            phone = firebaseUser.phoneNumber

                        )
                    )
                    _authUiState.value = AuthUiState.Success
                } ?: run {
                    _authUiState.value = AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
                }
            } catch (e: Exception) {
                handleAuthError(e)
            }
        }
    }

    fun registerUser(email: String, password: String) {
        if (!validate(email, password)) return
        setLoadingState()

        viewModelScope.launch {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { firebaseUser ->
                    saveUser(
                        User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName,
                            email = firebaseUser.email,
                            phone = firebaseUser.phoneNumber
                        )
                    )
                    _authUiState.value = AuthUiState.Success
                } ?: run {
                    _authUiState.value = AuthUiState.Error(message = AuthErrorType.GENERIC_ERROR)
                }
            } catch (e: Exception) {
                handleAuthError(e)
            }
        }
    }

    private fun saveUser(user: User) = viewModelScope.launch {
        try {
            userRepository.saveUser(user)
        } catch (e: Exception) {
            _authUiState.value = AuthUiState.Error(message = AuthErrorType.SAVE_USER_ERROR)
        }
    }

    private fun validate(email: String, password: String): Boolean {
        val emailResult = validateEmailUseCase(email)
        val passwordResult = validatePasswordUseCase(password)

        return when {
            !emailResult.isValid && !passwordResult.isValid -> {
                _authUiState.value = AuthUiState.Error(
                    emailError = AuthErrorType.INVALID_EMAIL,
                    passwordError = AuthErrorType.PASSWORD_SHORT
                )
                false
            }

            !emailResult.isValid -> {
                _authUiState.value = AuthUiState.Error(emailError = AuthErrorType.INVALID_EMAIL)
                false
            }

            !passwordResult.isValid -> {
                _authUiState.value = AuthUiState.Error(passwordError = AuthErrorType.PASSWORD_SHORT)
                false
            }

            else -> true
        }
    }

    private fun handleAuthError(exception: Exception) {
        val errorType = when (exception) {
            is FirebaseAuthInvalidCredentialsException ->
                AuthErrorType.INVALID_CREDENTIALS

            is FirebaseAuthInvalidUserException ->
                AuthErrorType.USER_NOT_FOUND

            is FirebaseAuthWeakPasswordException ->
                AuthErrorType.PASSWORD_SHORT

            is FirebaseNetworkException ->
                AuthErrorType.NETWORK_ERROR

            else -> AuthErrorType.GENERIC_ERROR
        }
        _authUiState.value = AuthUiState.Error(message = errorType)
    }

    private fun setLoadingState() {
        _authUiState.value = AuthUiState.Loading
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
        INVALID_CREDENTIALS,
        PASSWORD_SHORT,
        USER_NOT_FOUND,
        NETWORK_ERROR,
        SAVE_USER_ERROR,
        GENERIC_ERROR
    }
}
