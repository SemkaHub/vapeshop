package com.example.vapeshop.presentation.orders

import androidx.recyclerview.widget.DiffUtil
import com.example.vapeshop.domain.model.Order

class OrdersDiffCallback(
    private val oldList: List<Order>,
    private val newList: List<Order>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}