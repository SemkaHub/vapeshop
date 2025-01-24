package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCategoryBinding
import com.example.vapeshop.domain.factory.CategoryAdapterFactory
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import com.example.vapeshop.presentation.viewmodel.CategoryViewModel
import com.example.vapeshop.presentation.utils.GridConfigCalculator
import com.example.vapeshop.presentation.utils.SpacingItemDecoration
import com.example.vapeshop.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val binding by viewBinding(FragmentCategoryBinding::bind)
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    @Inject
    lateinit var categoryAdapterFactory: CategoryAdapterFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.setList(it)
        }
    }

    private fun initRecyclerView() {
        // Отступы между карточками
        val spacing =
            (16 * resources.displayMetrics.density).roundToInt()

        val screenWidth = requireActivity().windowManager.currentWindowMetrics.bounds.width()
        val density = resources.displayMetrics.density
        val calculator = GridConfigCalculator(density = density, screenWidth = screenWidth)

        // Вычисление количества колонок
        val spanCount = calculator.calculateSpanCount(spacing)
        // Вычисление ширины карточки
        val cardWidth = calculator.calculateCardWidth(spanCount, spacing)

        binding.categoriesRecyclerView.apply {
            categoryAdapter = categoryAdapterFactory.create(cardWidth) { categoryId ->
                openProductsByCategory(categoryId)
            }
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(SpacingItemDecoration(spacing, spanCount))
        }
    }

    private fun openProductsByCategory(categoryId: String) {
        findNavController().navigate(
            CategoryFragmentDirections.actionCategoryFragmentToProductListFragment(categoryId)
        )
    }
}