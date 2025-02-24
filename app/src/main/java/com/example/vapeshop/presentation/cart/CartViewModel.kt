package com.example.vapeshop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.Product
import com.example.vapeshop.domain.usecase.cart.AddItemToCartUseCase
import com.example.vapeshop.domain.usecase.cart.CalculateCartTotalUseCase
import com.example.vapeshop.domain.usecase.cart.ClearCartUseCase
import com.example.vapeshop.domain.usecase.cart.DebounceSyncCartUseCase
import com.example.vapeshop.domain.usecase.cart.GetCartFromServerUseCase
import com.example.vapeshop.domain.usecase.cart.GetCartUseCase
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
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val removeCartItemUseCase: RemoveCartItemUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val observeCartSyncStateUseCase: ObserveCartSyncStateUseCase,
    private val calculateCartTotalUseCase: CalculateCartTotalUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val debounceSyncCartUseCase: DebounceSyncCartUseCase,
    private val getCartFromServerUseCase: GetCartFromServerUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val state: StateFlow<CartUiState> = _state.asStateFlow()

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

    fun loadCartFromServer() {
        _state.value = CartUiState.Loading
        viewModelScope.launch {
            getCartFromServerUseCase()
            loadCartItems()
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            _state.value = CartUiState.Loading
            try {
                val cartItems = getCartUseCase()
                _localCart.value = cartItems
                updateState(cartItems)
            } catch (e: Exception) {
                _state.value =
                    CartUiState.Error(
                        message = e.message.toString(),
                        retryAction = { loadCartItems() })
            }
        }
    }

    fun addItemToCart(product: Product, quantity: Int) {
        viewModelScope.launch {
            addItemToCartUseCase(product, quantity)
            debounceSyncCartUseCase()
        }
    }

    fun increaseItemQuantity(productId: String) {
        viewModelScope.launch {
            val updatedCart = updateCartItemQuantityUseCase(productId, increase = true)
            debounceSyncCartUseCase()
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun decreaseItemQuantity(productId: String) {
        viewModelScope.launch {
            val updatedCart = updateCartItemQuantityUseCase(productId, increase = false)
            debounceSyncCartUseCase()
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun removeItemFromCart(productId: String) {
        viewModelScope.launch {
            val updatedCart = removeCartItemUseCase(productId)
            debounceSyncCartUseCase()
            _localCart.value = updatedCart
            updateState(updatedCart)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
            debounceSyncCartUseCase()
            loadCartItems()
        }
    }

    private fun updateState(cartItems: List<CartItem>) {
        _state.value = if (cartItems.isEmpty()) {
            CartUiState.Empty
        } else {
            CartUiState.Content(
                items = cartItems,
                totalPrice = calculateCartTotalUseCase(cartItems)
            )
        }
    }
}