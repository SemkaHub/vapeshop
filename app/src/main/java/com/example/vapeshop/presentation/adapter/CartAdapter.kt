package com.example.vapeshop.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vapeshop.databinding.ItemCartBinding
import com.example.vapeshop.domain.model.CartItem

class CartAdapter(
    private val onIncreaseClick: (String, Int) -> Unit,
    private val onDecreaseClick: (String, Int) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun setList(cartItems: List<CartItem>) {
        this.cartItems = cartItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.cartNameTextView.text = cartItem.product.name
            binding.cartPriceTextView.text = cartItem.product.price.toString()
            binding.quantityTextView.text = cartItem.quantity.toString()
            Glide.with(itemView.context).load(cartItem.product.imageUrl)
                .into(binding.cartImageView)

            binding.increaseButton.setOnClickListener {
                onIncreaseClick(cartItem.product.id.toString(), cartItem.quantity)
            }

            binding.decreaseButton.setOnClickListener {
                onDecreaseClick(cartItem.product.id.toString(), cartItem.quantity)
            }
        }
    }

}