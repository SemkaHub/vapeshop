package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun updateUserProfile(userProfile: UserProfile)
    suspend fun getUserProfile(): UserProfile?
}