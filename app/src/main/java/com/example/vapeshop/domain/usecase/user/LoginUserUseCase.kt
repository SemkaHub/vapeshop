package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val saveUserUseCase: SaveUserUseCase
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        val loginResult = authRepository.login(email, password)
        return loginResult.fold(
            onSuccess = { user ->
                saveUserUseCase(user)
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}