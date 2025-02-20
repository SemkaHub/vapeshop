package com.example.vapeshop.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.usecase.cart.GetCartFromServerUseCase
import com.example.vapeshop.domain.usecase.cart.GetCartItemCountUseCase
import com.example.vapeshop.domain.usecase.user.GetUserProfileFromServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getCartItemCountUseCase: GetCartItemCountUseCase,
    private val getUserProfileFromServerUseCase: GetUserProfileFromServerUseCase,
    private val getCartFromServerUseCase: GetCartFromServerUseCase,
) : ViewModel() {

    // Загружаем профиль пользователя и корзину с сервера при запуске приложения
    init {
        viewModelScope.launch {
            getUserProfileFromServerUseCase()
            try {
                getCartFromServerUseCase()
            } catch (_: Exception) {
                // If user not authenticated, do nothing
            }
        }
    }

    val cartItemCount: LiveData<Int> = getCartItemCountUseCase().asLiveData()
}