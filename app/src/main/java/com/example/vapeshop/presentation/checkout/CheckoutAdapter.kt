package com.example.vapeshop.presentation.checkout

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.vapeshop.databinding.ItemCheckoutBinding
import com.example.vapeshop.domain.model.CartItem
import java.util.Locale

class CheckoutAdapter(
    private val glide: RequestManager,
    private val errorDrawable: Drawable?,
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    private var products: List<CartItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CheckoutViewHolder {
        val binding =
            ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class CheckoutViewHolder(private val binding: ItemCheckoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            with(binding) {
                orderNameTextView.text = item.product.name
                orderPriceTextView.text =
                    String.format(Locale.getDefault(), "%.2f", item.product.price)
                quantityTextView.text = String.format(Locale.getDefault(), "%s", item.quantity)
                glide.load(item.product.imageUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(errorDrawable)
                    .into(orderImageView)
            }
        }
    }
}
