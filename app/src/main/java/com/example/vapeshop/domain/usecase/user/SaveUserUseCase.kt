package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) = userRepository.saveUser(user)
}