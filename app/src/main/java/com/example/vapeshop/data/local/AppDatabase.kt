package com.example.vapeshop.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, CartItemEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
}