package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetUserAddressUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke() : Address?{
        return userProfileRepository.getUserAddress()
    }
}