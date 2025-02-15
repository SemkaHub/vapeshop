package com.example.vapeshop.presentation.productlist

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.vapeshop.databinding.ItemProductBinding
import com.example.vapeshop.domain.model.Product
import java.util.Locale
import kotlin.math.roundToInt

const val DEFAULT_ITEM_QUANTITY = 1

class ProductListAdapter(
    private val cardWidth: Int,
    private val glide: RequestManager,
    private val errorDrawable: Drawable?,
    private val onAddToCartClick: (Product, Int) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

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
        val diffCallback = ProductListDiffCallback(this.products, products)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.products = products
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.progressBar.visibility = View.VISIBLE
            binding.nameTextView.text = product.name
            binding.priceTextView.text = String.format(Locale.getDefault(), "%.2f", product.price)
            binding.notAvailableTextView.visibility =
                if (product.isAvailable) View.GONE else View.VISIBLE
            binding.addToCartButton.visibility =
                if (product.isAvailable) View.VISIBLE else View.GONE

            glide.load(product.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(errorDrawable)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.imageView)

            binding.addToCartButton.setOnClickListener {
                product.let { it ->
                    onAddToCartClick.invoke(it, DEFAULT_ITEM_QUANTITY)
                }
            }
        }
    }
}