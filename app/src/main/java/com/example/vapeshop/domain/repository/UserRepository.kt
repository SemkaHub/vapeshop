package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun saveUser(user: User)
    suspend fun signOut()
}