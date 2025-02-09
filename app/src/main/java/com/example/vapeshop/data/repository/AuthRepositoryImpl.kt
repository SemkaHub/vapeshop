package com.example.vapeshop.data.repository

import com.example.vapeshop.data.extensions.toUser
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                Result.success(firebaseUser.toUser())
            }
                ?: Result.failure(Exception("Пользователь не найден")) // TODO: Replace with custom exception
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                Result.success(firebaseUser.toUser())
            }
                ?: Result.failure(Exception("Ошибка при регистрации пользователя")) // TODO: Replace with custom exception
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}