package com.example.vapeshop.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.usecase.cart.GetCartItemCountUseCase
import com.example.vapeshop.domain.usecase.user.GetUserProfileFromServerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getCartItemCountUseCase: GetCartItemCountUseCase,
    private val getUserProfileFromServerUseCase: GetUserProfileFromServerUseCase,
) : ViewModel() {

    // Загружаем профиль пользователя с сервера при запуске приложения
    init {
        viewModelScope.launch {
            getUserProfileFromServerUseCase()
        }
    }

    val cartItemCount: LiveData<Int> = getCartItemCountUseCase().asLiveData()
}