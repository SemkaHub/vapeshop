package com.example.vapeshop.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vapeshop.databinding.ItemOrderBinding
import com.example.vapeshop.domain.model.CartItem

class CheckoutAdapter : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    private var products: List<CartItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckoutViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CheckoutViewHolder,
        position: Int
    ) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    fun setList(products: List<CartItem>) {
        this.products = products
    }

    inner class CheckoutViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            with(binding) {
                orderNameTextView.text = item.product.name
                val totalPrice = item.product.price * item.quantity
                orderPriceTextView.text = totalPrice.toString()
                quantityTextView.text = item.quantity.toString()
                Glide.with(itemView.context).load(item.product.imageUrl)
                    .into(orderImageView)
            }
        }
    }
}
