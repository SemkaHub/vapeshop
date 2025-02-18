package com.example.vapeshop.domain.model

data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String? = null,
    val address: Address? = null,
)
