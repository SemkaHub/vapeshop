package com.example.vapeshop.presentation.cart

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vapeshop.databinding.ItemCartBinding
import com.example.vapeshop.domain.model.CartItem
import java.util.Locale

class CartAdapter(
    private val glide: RequestManager,
    private val errorDrawable: Drawable?,
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
        val diffCallback = CartDiffCallback(this.cartItems, cartItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.cartItems = cartItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            with(binding) {
                cartNameTextView.text = cartItem.product.name
                cartPriceTextView.text =
                    String.format(Locale.getDefault(), "%.2f", cartItem.product.price)
                quantityTextView.text = String.format(Locale.getDefault(), "%s", cartItem.quantity)
                glide.load(cartItem.product.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(errorDrawable)
                    .into(cartImageView)

                increaseButton.setOnClickListener {
                    onIncreaseClick(cartItem.product.id, cartItem.quantity)
                }

                decreaseButton.setOnClickListener {
                    onDecreaseClick(cartItem.product.id, cartItem.quantity)
                }

                removeButton.setOnClickListener {
                    onRemoveClick(cartItem.product.id)
                }
            }
        }
    }
}