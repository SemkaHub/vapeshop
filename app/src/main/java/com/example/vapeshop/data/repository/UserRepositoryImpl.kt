package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.local.CartDao
import com.example.vapeshop.data.local.UserDao
import com.example.vapeshop.data.mapper.toEntity
import com.example.vapeshop.data.mapper.toUser
import com.example.vapeshop.domain.repository.UserRepository
import com.example.vapeshop.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao,
    private val cartDao: CartDao
) : UserRepository {

    override suspend fun getCurrentUser(): User? = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.currentUser?.let { firebaseUser ->
                // Сначала пробуем получить из локальной БД
                userDao.getUserById(firebaseUser.uid)?.toUser()
                    ?: User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName,
                        email = firebaseUser.email,
                        phone = firebaseUser.phoneNumber
                    ).also {
                        saveUser(it)
                    }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting current user", e)
            throw Exception("Failed to get current user", e)
        }
    }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.IO) {
        try {
            userDao.insertUser(user.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save user", e)
        }
    }

    override suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signOut()
            userDao.deleteAllUsers()
            cartDao.clearCart()
        } catch (e: Exception) {
            throw Exception("Failed to sign out", e)
        }
    }
}
