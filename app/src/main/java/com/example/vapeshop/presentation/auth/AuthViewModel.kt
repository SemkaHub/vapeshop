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

    private val _authUiState = MutableLiveData<AuthState>(AuthState.Initial)
    val authUiState: LiveData<AuthState> = _authUiState

    fun loginUser(email: String, password: String) {
        executeAuthentication(email, password) { loginUserUseCase(email, password) }
    }

    fun registerUser(email: String, password: String) {
        executeAuthentication(email, password) { registerUserUseCase(email, password) }
    }

    private fun executeAuthentication(
        email: String,
        password: String,
        useCase: suspend () -> Result<Unit>
    ) {
        if (!validate(email, password)) return
        setLoadingState()

        viewModelScope.launch {
            val result = useCase()
            result.fold(
                onSuccess = {
                    _authUiState.value = AuthState.Success
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
                _authUiState.value = AuthState.Error(
                    emailError = AuthErrorType.INVALID_EMAIL,
                    passwordError = AuthErrorType.PASSWORD_SHORT
                )
                false
            }

            !emailResult.isValid -> {
                _authUiState.value = AuthState.Error(emailError = AuthErrorType.INVALID_EMAIL)
                false
            }

            !passwordResult.isValid -> {
                _authUiState.value = AuthState.Error(passwordError = AuthErrorType.PASSWORD_SHORT)
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
        _authUiState.value = AuthState.Error(message = errorType)
    }

    private fun setLoadingState() {
        _authUiState.value = AuthState.Loading
    }
}
