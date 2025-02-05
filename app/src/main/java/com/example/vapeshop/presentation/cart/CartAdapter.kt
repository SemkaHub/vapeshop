package com.example.vapeshop.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vapeshop.databinding.ItemCartBinding
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.presentation.cart.CartAdapter.MyDiffCallback
import java.util.Locale

class CartAdapter(
    private val onIncreaseClick: (String, Int) -> Unit,
    private val onDecreaseClick: (String, Int) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    class MyDiffCallback(
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
        val diffCallback = MyDiffCallback(this.cartItems, cartItems)
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
                Glide.with(itemView.context).load(cartItem.product.imageUrl)
                    .into(cartImageView)

                increaseButton.setOnClickListener {
                    onIncreaseClick(cartItem.product.id.toString(), cartItem.quantity)
                }

                decreaseButton.setOnClickListener {
                    onDecreaseClick(cartItem.product.id.toString(), cartItem.quantity)
                }

                removeButton.setOnClickListener {
                    onRemoveClick(cartItem.product.id.toString())
                }
            }
        }
    }
}