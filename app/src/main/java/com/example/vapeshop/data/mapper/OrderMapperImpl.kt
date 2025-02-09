package com.example.vapeshop.data.mapper

import com.example.vapeshop.data.extensions.toOrder
import com.example.vapeshop.domain.model.Order
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class OrderMapper @Inject constructor() {
    fun map(document: DocumentSnapshot): Order? {
        return document.toOrder()
    }
}