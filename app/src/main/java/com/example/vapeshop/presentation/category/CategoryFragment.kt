package com.example.vapeshop.presentation.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCategoryBinding
import com.example.vapeshop.utils.GridConfigCalculator
import com.example.vapeshop.utils.SpacingItemDecoration
import com.example.vapeshop.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private val binding by viewBinding(FragmentCategoryBinding::bind)
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

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
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCategories()
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collect { state ->
                    when (state) {
                        is CategoryState.Loading -> showLoading()
                        is CategoryState.Content -> showContent(state)
                        is CategoryState.Error -> showError(state)
                    }
                }
            }
        }
    }

    private fun hideAllViews() {
        with(binding) {
            loadingProgressBar.visibility = View.GONE
            categoriesRecyclerView.visibility = View.GONE
            errorState.visibility = View.GONE
        }
    }

    private fun showLoading() {
        hideAllViews()
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun showContent(state: CategoryState.Content) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.categoriesRecyclerView.visibility = View.VISIBLE
        categoryAdapter.setList(state.categories)
    }

    private fun showError(state: CategoryState.Error) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.errorState.visibility = View.VISIBLE
        binding.retryButton.setOnClickListener { state.retryAction() }
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
        // Изображение в случае ошибки загрузки картинки
        val errorDrawable = getDrawable(requireContext(), R.drawable.load_drawable_error)

        binding.categoriesRecyclerView.apply {
            categoryAdapter = CategoryAdapter(cardWidth, errorDrawable) { categoryId ->
                openProductsByCategory(categoryId)
            }
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(SpacingItemDecoration(spacing, spanCount))
        }
    }

    private fun openProductsByCategory(categoryId: String) {
        findNavController().navigate(
            CategoryFragmentDirections.actionCategoryFragmentToProductListFragment(
                categoryId
            )
        )
    }
}