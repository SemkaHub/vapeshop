package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.repository.UserRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.signOut()
    }
}