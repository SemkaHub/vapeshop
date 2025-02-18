package com.example.vapeshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.vapeshop.data.local.entity.UserProfileEntity

@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)

    @Query("SELECT * FROM user_profile")
    suspend fun getUserProfile(): UserProfileEntity?

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}