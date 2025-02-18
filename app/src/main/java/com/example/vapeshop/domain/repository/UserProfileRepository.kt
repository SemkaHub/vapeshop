package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun updateUserProfile(userProfile: UserProfile)
    suspend fun getUserProfileFromServer(): UserProfile?
    suspend fun getUserProfileFromLocal(): UserProfile?
    suspend fun getUserAddress(): Address?
}