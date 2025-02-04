package com.example.vapeshop.domain.repository

import com.example.vapeshop.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
}