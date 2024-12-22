package com.example.vapeshop.data.repository

import com.example.vapeshop.data.local.UserDao
import com.example.vapeshop.data.local.UserEntity
import com.example.vapeshop.domain.UserRepository
import com.example.vapeshop.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) :
    UserRepository {

    override suspend fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let {
            User(id = it.uid, name = it.displayName, email = it.email, phone = it.phoneNumber)
        }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(
            UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                phone = user.phone
            )
        )

    }

}
