package com.example.vapeshop.domain.usecase.user

import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.UserProfile
import javax.inject.Inject

class SaveUserAddressUseCase @Inject constructor(
    private val getUserProfileFromLocalUseCase: GetUserProfileFromLocalUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) {
    suspend operator fun invoke(address: Address) {
        val userProfile = getUserProfileFromLocalUseCase()
        val updatedProfile = userProfile?.copy(address = address) ?: UserProfile(address = address)
        updateUserProfileUseCase(updatedProfile)
    }
}