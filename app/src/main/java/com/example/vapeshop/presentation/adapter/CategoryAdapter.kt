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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.example.vapeshop.databinding.ItemCategoryBinding
import com.example.vapeshop.domain.model.Category
import com.example.vapeshop.domain.util.ResourceProvider
import kotlin.math.roundToInt

class CategoryAdapter(
    private val cardWidth: Int,
    private val resourceProvider: ResourceProvider,
    private val onItemClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categories: List<Category> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val params = binding.root.layoutParams
        params.width = cardWidth
        params.height =
            (cardWidth * 1.5).roundToInt() // The height of the card is 1.5 times its width.
        binding.root.layoutParams = params
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = categories.size

    fun setList(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.nameTextView.text = category.name
            binding.progressBar.visibility = View.VISIBLE
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(resourceProvider.getErrorImage())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        Log.d("CategoryAdapter", "onLoadFailed: ${e?.message}")
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

            itemView.setOnClickListener {
                category.id?.let {
                    onItemClick?.invoke(it)
                }
            }
        }
    }
}