package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentShopBinding
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import com.example.vapeshop.presentation.viewmodel.MainViewModel
import com.example.vapeshop.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ShopFragment : Fragment() {

    private val binding by viewBinding(FragmentShopBinding::bind)
    private val viewModel: MainViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.setList(it)
        }
    }

    private fun setupRecyclerView() {
        val spacing =
            (16 * resources.displayMetrics.density).roundToInt()   // Отступы между карточками
        val spanCount = calculateSpanCount(spacing)            // Вычисление количества колонок
        val cardWidth = calculateCardWidth(spanCount, spacing)  // Вычисление ширины карточки

        initRecyclerView(spanCount, spacing, cardWidth)
    }

    private fun initRecyclerView(spanCount: Int, spacing: Int, cardWidth: Int) {
        binding.categoriesRecyclerView.apply {
            categoryAdapter = CategoryAdapter(cardWidth) { categoryId ->
                openProductsByCategory(categoryId)
            }
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(CategoryAdapter.MyItemDecoration(spacing))
        }
    }

    private fun openProductsByCategory(categoryId: String) {
        val productListFragment = ProductListFragment().apply {
            arguments = Bundle().apply {
                putString("categoryId", categoryId)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, productListFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun calculateSpanCount(spacing: Int): Int {
        // Нужна реальная ширина экрана. Метод resources.displayMetrics.widthPixels не подходит, так
        // как не учитывает место, зарезервиванное системой, из-за чего отступы у карточек будут разными.
        val screenWidth = requireActivity().windowManager.currentWindowMetrics.bounds.width()
        var spanCount = 1
        val range =
            (150 * resources.displayMetrics.density).roundToInt()..
                    (300 * resources.displayMetrics.density).roundToInt()

        // if card width in 150..300 in dp
        for (i in 2..6) {
            if (screenWidth / i - spacing * (i + 1) in range) spanCount = i
        }
        return spanCount
    }

    private fun calculateCardWidth(spanCount: Int, spacing: Int): Int {
        val screenWidth = requireActivity().windowManager.currentWindowMetrics.bounds.width()
        val totalSpacing = (spanCount + 1) * spacing
        return ((screenWidth - totalSpacing) / spanCount.toFloat()).roundToInt()
    }
}