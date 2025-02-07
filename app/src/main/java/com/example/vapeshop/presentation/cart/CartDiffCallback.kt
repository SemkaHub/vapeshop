package com.example.vapeshop.presentation.cart

import androidx.recyclerview.widget.DiffUtil
import com.example.vapeshop.domain.model.CartItem

internal class CartDiffCallback (
    private val oldList: List<CartItem>,
    private val newList: List<CartItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].product.id == newList[newItemPosition].product.id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}