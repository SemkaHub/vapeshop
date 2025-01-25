package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CartState>(CartState.Loading)
    val state: StateFlow<CartState> = _state

    fun loadCartItems() {
        viewModelScope.launch {
            _state.value = CartState.Loading
            try {
                val cartItems = cartRepository.getCartItems()
                _state.value = if (cartItems.isEmpty()) {
                    CartState.Empty
                } else {
                    val totalPrice = calculateTotalPrice(cartItems)
                    CartState.Content(cartItems, totalPrice)
                }
            } catch (e: Exception) {
                _state.value =
                    CartState.Error(
                        message = e.message.toString(),
                        retryAction = { loadCartItems() })
            }
        }
    }


    fun addItemToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            cartRepository.addToCart(product, quantity)
            loadCartItems()
        }
    }

    fun increaseItemQuantity(productId: String) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
            loadCartItems()
        }
    }

    fun decreaseItemQuantity(productId: String) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(productId)
            loadCartItems()
        }
    }

    fun calculateTotalPrice(cartItems: List<CartItem>): Double {
        return cartItems.fold(0.0) { acc, cartItem ->
            acc + (cartItem.product.price * cartItem.quantity) }
    }

    fun removeItemFromCart(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
            loadCartItems()
        }
    }

    sealed class CartState {
        object Loading : CartState()
        data class Content(val items: List<CartItem>, val totalPrice: Double) : CartState()
        object Empty : CartState()
        data class Error(val message: String, val retryAction: () -> Unit) : CartState()
    }
}