package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.extensions.toDomain
import com.example.vapeshop.data.extensions.toEntity
import com.example.vapeshop.data.local.dao.UserProfileDao
import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userProfileDao: UserProfileDao,
) : UserProfileRepository {

    override suspend fun updateUserProfile(userProfile: UserProfile) {
        try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("User not authenticated")
            val uid = currentUser.uid
            userProfileDao.insertUserProfile(userProfile.toEntity())
            firestore.collection("users").document(uid).set(userProfile).await()
        } catch (e: Exception) {
            throw Exception("Failed to update user profile", e)
        }
    }

    override suspend fun getUserProfileFromServer(): UserProfile? {
        try {
            // Получаем профиль с сервера
            val profile = firebaseAuth.currentUser?.let { firebaseUser ->
                val snapshot =
                    firestore.collection("users").document(firebaseUser.uid).get().await()
                val addressMap = snapshot.get("address") as Map<*, *>
                val address = Address(
                    city = (addressMap["city"] ?: "").toString(),
                    street = (addressMap["street"] ?: "").toString(),
                    apartment = (addressMap["apartment"] ?: "").toString(),
                )
                UserProfile(
                    firstName = snapshot.getString("firstName") ?: "",
                    lastName = snapshot.getString("lastName") ?: "",
                    phoneNumber = snapshot.getString("phone") ?: "",
                    profilePhotoUrl = snapshot.getString("profileImageUrl") ?: "",
                    address = address
                )
            }
            // Обновляем профиль в локальной базе данных
            if (profile != null) userProfileDao.insertUserProfile(userProfile = profile.toEntity())

            return profile
        } catch (e: Exception) {
            Log.d("UserProfileRepository", "Failed to get user profile from server", e)
            return null
        }
    }

    override suspend fun getUserProfileFromLocal(): UserProfile? {
        val userProfile = userProfileDao.getUserProfile()?.toDomain()
        return userProfile
    }

    override suspend fun getUserAddress(): Address? {
        return getUserProfileFromLocal()?.address
    }
}