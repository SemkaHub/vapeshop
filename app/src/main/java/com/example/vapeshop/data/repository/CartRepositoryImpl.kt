package com.example.vapeshop.data.repository

import android.util.Log
import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.CartResponse
import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : CartRepository {

    private val syncStateFlow = MutableStateFlow(false)

    private var syncJob: Job? = null

    override fun observeSyncState(): Flow<Boolean> = syncStateFlow.asStateFlow()

    override suspend fun addToCart(product: Product, quantity: Int) {
        syncJob?.cancel()
        // Локальное обновление корзины
        updateLocalCart(product, quantity)
        // Создаем новую отложенную синхронизацию
        startSyncJob()
    }

    private suspend fun updateLocalCart(product: Product, quantity: Int) {
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
    }

    override suspend fun getCart(): List<CartItem> {
        return try {
            // Получаем корзину с сервера
            if (firebaseAuth.currentUser != null) {
                updateCartFromServer()
            }

            // Получаем локальную корзину
            getLocalCart()
        } catch (e: Exception) {
            // Если не удалось получить данные с сервера, возвращаем локальные данные
            Log.e("CartSync", "Failed to sync with server, using local data: ${e.message}")
            getLocalCart()
        }
    }

    override suspend fun getLocalCart(): List<CartItem> {
        return localDataSource.getCartItems().map { cartItemEntity ->
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

    override fun getCartItemCount(): Flow<Int> =
        localDataSource.getCartItemCount().map { it ?: 0 }

    override suspend fun increaseQuantity(productId: String): List<CartItem> {
        // Локальное обновление
        val cartItem = localDataSource.getCartItemById(productId)
        if (cartItem != null) {
            localDataSource.updateQuantity(productId, cartItem.quantity + 1)
        }

        // Запуск синхронизации в фоне
        startSyncJob()

        // Возвращаем локальные данные
        return getLocalCart()
    }

    override suspend fun decreaseQuantity(productId: String): List<CartItem> {
        val cartItem = localDataSource.getCartItemById(productId)
        if (cartItem != null && cartItem.quantity > 1) {
            // Уменьшаем количество товара
            localDataSource.updateQuantity(productId, cartItem.quantity - 1)
        }

        // Запуск синхронизации в фоне
        startSyncJob()

        // Возвращаем локальные данные
        return getLocalCart()
    }

    override suspend fun removeFromCart(productId: String): List<CartItem> {
        localDataSource.deleteCartItemById(productId)

        // Запуск синхронизации в фоне
        startSyncJob()

        // Возвращаем локальные данные
        return getLocalCart()
    }

    private fun startSyncJob() {
        syncJob?.cancel()   // Отменяем предыдущую синхронизацию
        syncJob = CoroutineScope(Dispatchers.IO).launch {
            syncStateFlow.value = true  // Начало синхронизации
            delay(1500)     // Ожидаем 1.5 секунды перед синхронизацией
            syncCartWithServer()
            syncStateFlow.value = false // Конец синхронизации
        }
    }

    override suspend fun clearCart() {
        localDataSource.clearCart()
        syncCartWithServer()
    }

    private suspend fun syncCartWithServer() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val localCart = localDataSource.getCartItems()
            try {
                val cartData = CartResponse(items = localCart)
                firestore.collection("carts")
                    .document(userId)
                    .set(cartData)
                    .await()
            } catch (e: Exception) {
                Log.e("CartSync", "Failed to sync cart with server: ${e.message}")
            }
        } else {
            Log.e("CartSync", "User is not authenticated")
        }
    }

    private suspend fun updateCartFromServer() {
        try {
            val userId = firebaseAuth.currentUser?.uid ?: throw Exception("User not authenticated")
            val snapshot = firestore.collection("carts").document(userId).get().await()
            snapshot.toObject(CartResponse::class.java)?.items?.forEach { remoteItem ->
                localDataSource.insertCartItem(remoteItem)
            } ?: throw Exception("Cart is empty")
        } catch (e: Exception) {
            Log.e("CartUpdate", "Failed to update cart with server: ${e.message}")
        }
    }
}

