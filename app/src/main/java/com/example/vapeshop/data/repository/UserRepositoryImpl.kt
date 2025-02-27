package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.extensions.toEntity
import com.example.vapeshop.data.extensions.toUser
import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.local.dao.UserDao
import com.example.vapeshop.data.local.dao.UserProfileDao
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao,
    private val cartDao: CartDao,
    private val userProfileDao: UserProfileDao,
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        return try {
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

    override suspend fun saveUser(user: User) {
        try {
            userDao.insertUser(user.toEntity())
        } catch (e: Exception) {
            throw Exception("Failed to save user", e)
        }
    }

    override suspend fun signOut() {
        try {
            firebaseAuth.signOut()
            userDao.deleteAllUsers()
            cartDao.clearCart()
            userProfileDao.deleteUserProfile()
        } catch (e: Exception) {
            throw Exception("Failed to sign out", e)
        }
    }
}
