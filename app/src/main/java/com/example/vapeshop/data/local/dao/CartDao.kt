package com.example.vapeshop.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vapeshop.data.local.entity.CartItemEntity

@Dao
interface CartDao {

    @Query("SELECT * FROM cart")
    suspend fun getCartItems(): List<CartItemEntity>

    @Query("SELECT * FROM cart WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemById(productId: String): CartItemEntity?

    @Query("UPDATE cart SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun deleteCartItemById(productId: String)

    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)
}