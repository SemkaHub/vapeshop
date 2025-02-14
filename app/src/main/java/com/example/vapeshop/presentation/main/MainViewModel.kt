package com.example.vapeshop.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.vapeshop.domain.usecase.cart.GetCartItemCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getCartItemCountUseCase: GetCartItemCountUseCase,
) : ViewModel() {
    val cartItemCount: LiveData<Int> = getCartItemCountUseCase().asLiveData()
}