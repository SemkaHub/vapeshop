package com.example.vapeshop.presentation.auth

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(
        val message: AuthErrorType? = null,
        val emailError: AuthErrorType? = null,
        val passwordError: AuthErrorType? = null
    ) : AuthState()
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