package com.example.vapeshop.data.repository

import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserProfileRepository {
    override suspend fun updateUserProfile(userProfile: UserProfile) {
        try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("User not authenticated")
            firestore.collection("users").document(currentUser.uid).set(userProfile).await()
        } catch (e: Exception) {
            throw Exception("Failed to update user profile", e)
        }
    }

    override suspend fun getUserProfile(): UserProfile? {
        val profile = firebaseAuth.currentUser?.let { firebaseUser ->
            val snapshot =
                firestore.collection("users").document(firebaseUser.uid).get().await()
            snapshot.toObject(UserProfile::class.java)
        }
        return profile
    }
}