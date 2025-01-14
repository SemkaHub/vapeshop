package com.example.vapeshop.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.CartRepository
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val cartItems = cartRepository.getCartItems()
            _cartItems.value = cartItems
            calculateTotalPrice(cartItems)
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

    fun removeItemFromCart(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
            loadCartItems()
        }
    }

    fun calculateTotalPrice(cartItems: List<CartItem>) {
        var totalPrice = 0.0
        cartItems.forEach { it ->
            totalPrice += it.product.price * it.quantity
        }
        _totalPrice.value = totalPrice
    }
}