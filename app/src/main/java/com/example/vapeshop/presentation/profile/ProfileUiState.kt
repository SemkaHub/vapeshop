package com.example.vapeshop.presentation.profile

import com.example.vapeshop.domain.model.User

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object UnAuthorized : ProfileUiState()
}