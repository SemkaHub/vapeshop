package com.example.vapeshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.local.dao.UserDao
import com.example.vapeshop.data.local.dao.UserProfileDao
import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.data.local.entity.UserEntity
import com.example.vapeshop.data.local.entity.UserProfileEntity

@Database(
    entities = [UserEntity::class, CartItemEntity::class, UserProfileEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun userProfileDao(): UserProfileDao
}