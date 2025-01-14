package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.local.CartDao
import com.example.vapeshop.data.local.CartItemEntity
import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.ProductRepository
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartDao,
    private val remoteDataSource: ProductRepository,
    private val firebaseAuth: FirebaseAuth
) : CartRepository {

    override suspend fun addToCart(product: Product, quantity: Int) =
        withContext(Dispatchers.IO) {
            // Текущее состояние товара
            val currentCartItem = localDataSource.getCartItemById(product.id)

            if (currentCartItem != null) {
                // Если товар уже в корзине, то увеличиваем количество
                val updatedQuantity = currentCartItem.quantity + quantity
                localDataSource.updateQuantity(product.id, updatedQuantity)
            } else {
                // Добавляем новый товар в корзину
                val newCartItem = CartItemEntity(
                    productId = product.id,
                    name = product.name,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    quantity = quantity
                )
                localDataSource.insertCartItem(newCartItem)
            }

            // Синхронизируем изменения с сервером
            syncCartWithServer()
        }

    override suspend fun getCartItems(): List<CartItem> = withContext(Dispatchers.IO) {
        // Получаем локальную корзину
        val localCart = async { localDataSource.getCartItems() }
        // Получаем корзину с сервера
        val serverUpdate = async {
            if (firebaseAuth.currentUser != null) {
                updateCartFromServer()
            }
        }

        serverUpdate.await()
        // Обновляем локальную корзину
        localCart.await().map { cartItemEntity ->
            CartItem(
                product = Product(
                    id = cartItemEntity.productId,
                    name = cartItemEntity.name,
                    price = cartItemEntity.price,
                    imageUrl = cartItemEntity.imageUrl
                ),
                quantity = cartItemEntity.quantity
            )
        }
    }

    override suspend fun increaseQuantity(productId: String) = withContext(Dispatchers.IO) {
        val cartItem = localDataSource.getCartItemById(productId)
        if (cartItem != null) {
            localDataSource.updateQuantity(productId, cartItem.quantity + 1)

            // Синхронизируем изменения с сервером
            syncCartWithServer()
        }
    }

    override suspend fun decreaseQuantity(productId: String) = withContext(Dispatchers.IO) {
        val cartItem = localDataSource.getCartItemById(productId)
        if (cartItem != null) {
            if (cartItem.quantity > 1) {
                // Уменьшаем количество товара
                localDataSource.updateQuantity(productId, cartItem.quantity - 1)
            } else {
                // Если количество равно 1, удаляем товар из корзины
                localDataSource.deleteCartItemById(productId)
            }

            // Синхронизируем изменения с сервером
            syncCartWithServer()
        }
    }

    override suspend fun removeFromCart(productId: String) = withContext(Dispatchers.IO) {
        localDataSource.deleteCartItemById(productId)
        syncCartWithServer()
    }

    private suspend fun syncCartWithServer(): Unit = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val localCart = localDataSource.getCartItems()
            try {
                remoteDataSource.updateCart(userId, localCart)
            } catch (e: Exception) {
                Log.e("CartSync", "Failed to sync cart with server: ${e.message}")
            }
        } else {
            Log.e("CartSync", "User is not authenticated")
        }
    }

    private suspend fun updateCartFromServer(): Unit = withContext(Dispatchers.IO) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            try {
                val remoteCart = remoteDataSource.getCart(userId)
                localDataSource.clearCart()
                remoteCart.forEach { remoteItem ->
                    localDataSource.insertCartItem(
                        CartItemEntity(
                            productId = remoteItem.productId,
                            quantity = remoteItem.quantity,
                            name = remoteItem.name,
                            imageUrl = remoteItem.imageUrl,
                            price = remoteItem.price
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("CartUpdate", "Failed to update cart with server: ${e.message}")
            }
        } else {
            Log.e("CartUpdate", "User is not authenticated")
        }
    }
}
