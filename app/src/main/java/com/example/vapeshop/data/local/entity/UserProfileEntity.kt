package com.example.vapeshop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Byte = 1,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val city: String?,
    val apartment: String?,
    val street: String?,
    val photoUrl: String?,
)