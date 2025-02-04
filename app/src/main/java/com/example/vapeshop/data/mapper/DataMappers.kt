package com.example.vapeshop.data.mapper

import com.example.vapeshop.data.local.entity.UserEntity
import com.example.vapeshop.domain.model.User
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