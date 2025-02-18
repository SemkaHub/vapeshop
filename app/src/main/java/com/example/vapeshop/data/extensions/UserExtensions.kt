package com.example.vapeshop.data.extensions

import com.example.vapeshop.data.local.entity.UserEntity
import com.example.vapeshop.data.local.entity.UserProfileEntity
import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.domain.model.UserProfile
import com.google.firebase.auth.FirebaseUser

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone
)

fun UserEntity.toUser() = User(
    id = id,
    name = name,
    email = email,
    phone = phone
)

fun FirebaseUser.toUser() = User(
    id = uid,
    name = displayName,
    email = email,
    phone = phoneNumber
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    city = address?.city,
    apartment = address?.apartment,
    street = address?.street,
    photoUrl = profilePhotoUrl
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    phoneNumber = phoneNumber ?: "",
    address = if (city.isNullOrEmpty() || apartment.isNullOrEmpty() || street.isNullOrEmpty()) null
    else Address(
        city = city,
        apartment = apartment,
        street = street
    ),
    profilePhotoUrl = photoUrl
)