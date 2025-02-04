package com.example.vapeshop.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.usecase.user.LoginUserUseCase
import com.example.vapeshop.domain.usecase.user.RegisterUserUseCase
import com.example.vapeshop.domain.usecase.validation.ValidateEmailUseCase
import com.example.vapeshop.domain.usecase.validation.ValidatePasswordUseCase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
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
            val result = loginUserUseCase(email, password)
            result.fold(
                onSuccess = {
                    _authUiState.value = AuthUiState.Success
                },
                onFailure = { throwable ->
                    val exception = throwable as? Exception ?: Exception(throwable)
                    handleAuthError(exception)
                }
            )
        }
    }

    fun registerUser(email: String, password: String) {
        if (!validate(email, password)) return
        setLoadingState()

        viewModelScope.launch {
            val result = registerUserUseCase(email, password)
            result.fold(
                onSuccess = {
                    _authUiState.value = AuthUiState.Success
                },
                onFailure = { throwable ->
                    val exception = throwable as? Exception ?: Exception(throwable)
                    handleAuthError(exception)
                }
            )
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
