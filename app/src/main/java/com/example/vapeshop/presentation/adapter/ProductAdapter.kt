package com.example.vapeshop.presentation.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.vapeshop.databinding.ItemProductBinding
import com.example.vapeshop.domain.model.Product
import kotlin.math.roundToInt

class ProductAdapter(
    private val cardWidth: Int,
    private val onAddToCartClick: (String) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<Product> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params = binding.root.layoutParams
        params.width = cardWidth
        params.height = (cardWidth * 1.5).roundToInt()
        binding.root.layoutParams = params
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    fun setList(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.progressBar.visibility = View.VISIBLE
            binding.nameTextView.text = product.name
            binding.priceTextView.text = product.price.toString()
            binding.notAvailableTextView.visibility =
                if (product.isAvailable) View.GONE else View.VISIBLE
            binding.addToCartButton.visibility =
                if (product.isAvailable) View.VISIBLE else View.GONE

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        binding.imageView.setImageResource(android.R.drawable.stat_notify_error)
                        Log.d("ProductAdapter", "onLoadFailed: ${e?.message}")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imageView)

            binding.addToCartButton.setOnClickListener {
                product.id?.let { id ->
                    onAddToCartClick.invoke(id)
                }
            }
        }
    }
}