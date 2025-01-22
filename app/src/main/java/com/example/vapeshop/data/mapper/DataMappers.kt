package com.example.vapeshop.data.mapper

import com.example.vapeshop.data.local.UserEntity
import com.example.vapeshop.domain.model.User

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