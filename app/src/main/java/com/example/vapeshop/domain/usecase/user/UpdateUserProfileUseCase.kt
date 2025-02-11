package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile) {
        userProfileRepository.updateUserProfile(userProfile)
    }
}