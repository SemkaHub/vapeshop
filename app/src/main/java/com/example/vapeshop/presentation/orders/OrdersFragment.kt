package com.example.vapeshop.presentation.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentOrdersBinding
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val binding by viewBinding(FragmentOrdersBinding::bind)
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        setupSwipeRefreshLayout()
        setupObservers()
    }

    private fun initRecyclerView() {
        val strings = resourceProvider.getOrdersAdapterStrings()
        val colors = resourceProvider.getOrdersAdapterColors()
        ordersAdapter = OrdersAdapter(strings, colors)
        binding.ordersRecyclerView.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getOrders()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is OrdersUiState.Loading -> showLoading()
                        is OrdersUiState.Empty -> showEmpty()
                        is OrdersUiState.Content -> showContent(state.orders)
                        is OrdersUiState.Error -> showError(state.message, state.retryAction)
                    }
                }
            }
        }
    }

    private fun hideAllViews() {
        with(binding) {
            loadingProgressBar.visibility = View.GONE
            ordersRecyclerView.visibility = View.GONE
            emptyState.visibility = View.GONE
            errorState.visibility = View.GONE
        }
    }

    private fun showLoading() {
        hideAllViews()
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.loadingProgressBar.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError(message: String, retryAction: () -> Unit) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.errorState.visibility = View.VISIBLE
        Log.d("OrdersFragment", "Error message: $message")
        binding.retryButton.setOnClickListener { retryAction() }
    }

    private fun showContent(orders: List<Order>) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.ordersRecyclerView.visibility = View.VISIBLE
        ordersAdapter.setList(orders)
    }
}