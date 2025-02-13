package com.example.vapeshop.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val product: Product,
    val quantity: Int
) : Parcelable
