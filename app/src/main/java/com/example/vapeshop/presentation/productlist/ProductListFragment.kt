package com.example.vapeshop.presentation.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentProductListBinding
import com.example.vapeshop.presentation.cart.CartViewModel
import com.example.vapeshop.presentation.common.utils.GridConfigCalculator
import com.example.vapeshop.presentation.common.utils.SpacingItemDecoration
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val binding by viewBinding(FragmentProductListBinding::bind)
    private val viewModel: ProductListViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var productListAdapter: ProductListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = arguments?.getString("categoryId") ?: ""

        setupSearchMenu()
        initRecyclerView()
        setupSwipeRefresh(categoryId)
        initObservers(categoryId)
        setupGoToShopButton()
    }

    private fun setupGoToShopButton() {
        binding.goToShopButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupSearchMenu() {
        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(
                menu: Menu, menuInflater: MenuInflater
            ) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val searchView = menu.findItem(R.id.action_search).actionView as SearchView
                searchView.setOnQueryTextListener(object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initRecyclerView() {
        // Отступы между карточками
        val spacing = (16 * resources.displayMetrics.density).roundToInt()

        val screenWidth = requireActivity().windowManager.currentWindowMetrics.bounds.width()
        val density = resources.displayMetrics.density
        val calculator = GridConfigCalculator(density = density, screenWidth = screenWidth)

        // Вычисление количества колонок
        val spanCount = calculator.calculateSpanCount(spacing)
        // Вычисление ширины карточки
        val cardWidth = calculator.calculateCardWidth(spanCount, spacing)
        // Изображение в случае ошибки загрузки картинки
        val errorDrawable = getDrawable(requireContext(), R.drawable.load_drawable_error)

        productListAdapter =
            ProductListAdapter(cardWidth, glide, errorDrawable) { product, quantity ->
                cartViewModel.addItemToCart(product, quantity)
            }
        binding.productsRecyclerView.apply {
            adapter = productListAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(SpacingItemDecoration(spacing, spanCount))
        }
    }

    private fun setupSwipeRefresh(categoryId: String) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getProducts(categoryId)
        }
    }

    private fun initObservers(categoryId: String) {
        viewModel.getProducts(categoryId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collect { state ->
                    when (state) {
                        is ProductListUiState.Loading -> showLoading()
                        is ProductListUiState.Content -> showContent(state)
                        is ProductListUiState.Empty -> showEmptyState()
                        is ProductListUiState.Error -> showError(state)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        hideAllViews()
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun showContent(state: ProductListUiState.Content) {
        hideAllViews()
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            productsRecyclerView.visibility = View.VISIBLE

            // Обновляем список
            productListAdapter.setList(state.products)
        }
    }


    private fun showEmptyState() {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError(error: ProductListUiState.Error) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.errorState.visibility = View.VISIBLE
        binding.retryButton.setOnClickListener { error.retryAction() }
    }

    private fun hideAllViews() {
        with(binding) {
            productsRecyclerView.visibility = View.GONE
            loadingProgressBar.visibility = View.GONE
            emptyState.visibility = View.GONE
            errorState.visibility = View.GONE
        }
    }
}