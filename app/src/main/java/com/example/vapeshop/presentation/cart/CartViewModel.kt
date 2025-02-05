package com.example.vapeshop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.usecase.cart.AddToCartUseCase
import com.example.vapeshop.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.vapeshop.domain.usecase.cart.ClearCartUseCase
import com.example.vapeshop.domain.usecase.cart.GetCartItemsUseCase
import com.example.vapeshop.domain.usecase.cart.ObserveCartSyncStateUseCase
import com.example.vapeshop.domain.usecase.cart.RemoveCartItemUseCase
import com.example.vapeshop.domain.usecase.cart.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val observeCartSyncStateUseCase: ObserveCartSyncStateUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CartState>(CartState.Loading)
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _localCart = MutableStateFlow<List<CartItem>>(emptyList())
    val localCart: StateFlow<List<CartItem>> = _localCart.asStateFlow()

    init {
        observeSyncState()
    }

    private fun observeSyncState() {
        viewModelScope.launch {
            observeCartSyncStateUseCase().collect { isSyncing ->
                _isSyncing.value = isSyncing
            }
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            _state.value = CartState.Loading
            try {
                val cartItems = getCartItemsUseCase()
                _localCart.value = cartItems
                updateState(cartItems)
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
            addToCartUseCase(product, quantity)
            loadCartItems()
        }
    }

    fun increaseItemQuantity(productId: String) {
        viewModelScope.launch {
            val updatedCart = updateCartItemQuantityUseCase(productId, increase = true)
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun decreaseItemQuantity(productId: String) {
        viewModelScope.launch {
            val updatedCart = updateCartItemQuantityUseCase(productId, increase = false)
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun removeItemFromCart(productId: String) {
        viewModelScope.launch {
            val updatedCart = removeCartItemUseCase(productId)
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
            loadCartItems()
        }
    }

    private fun updateState(cartItems: List<CartItem>) {
        _state.value = if (cartItems.isEmpty()) {
            CartState.Empty
        } else {
            CartState.Content(
                items = cartItems,
                totalPrice = calculateCartTotalUseCase(cartItems)
            )
        }
    }
}