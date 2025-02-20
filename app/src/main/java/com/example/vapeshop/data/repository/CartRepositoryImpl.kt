package com.example.vapeshop.data.repository

import com.example.vapeshop.data.local.dao.CartDao
import com.example.vapeshop.data.local.entity.CartItemEntity
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val localDataSource: CartDao,
) : CartRepository {

    override suspend fun addItemToCart(product: Product, quantity: Int) {
        updateLocalCart(product, quantity)
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
        // Возвращаем локальные данные
        return getLocalCart()
    }

    override suspend fun decreaseQuantity(productId: String): List<CartItem> {
        val cartItem = localDataSource.getCartItemById(productId)
        if (cartItem != null && cartItem.quantity > 1) {
            // Уменьшаем количество товара
            localDataSource.updateQuantity(productId, cartItem.quantity - 1)
        }
        // Возвращаем локальные данные
        return getLocalCart()
    }

    override suspend fun removeFromCart(productId: String): List<CartItem> {
        localDataSource.deleteCartItemById(productId)
        // Возвращаем локальные данные
        return getLocalCart()
    }

    override suspend fun clearCart() {
        localDataSource.clearCart()
    }
}

