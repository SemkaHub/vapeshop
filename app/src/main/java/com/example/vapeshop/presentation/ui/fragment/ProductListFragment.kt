package com.example.vapeshop.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentProductListBinding
import com.example.vapeshop.presentation.adapter.ProductAdapter
import com.example.vapeshop.presentation.adapter.factory.ProductAdapterFactory
import com.example.vapeshop.presentation.viewmodel.CartViewModel
import com.example.vapeshop.presentation.viewmodel.ProductViewModel
import com.example.vapeshop.utils.GridConfigCalculator
import com.example.vapeshop.utils.SpacingItemDecoration
import com.example.vapeshop.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val binding by viewBinding(FragmentProductListBinding::bind)
    private val viewModel: ProductViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    @Inject
    lateinit var productAdapterFactory: ProductAdapterFactory

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
        initObservers(categoryId)
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

        productAdapter = productAdapterFactory.create(cardWidth) { product, quantity ->
            cartViewModel.addItemToCart(product, quantity)
        }
        binding.productsRecyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(SpacingItemDecoration(spacing, spanCount))
        }
    }

    private fun initObservers(categoryId: String) {
        viewModel.getProducts(categoryId)

        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.setList(products)
        }
    }
}