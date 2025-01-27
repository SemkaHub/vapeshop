package com.example.vapeshop.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCartBinding
import com.example.vapeshop.presentation.adapter.CartAdapter
import com.example.vapeshop.presentation.utils.viewBinding
import com.example.vapeshop.presentation.viewmodel.CartViewModel
import com.example.vapeshop.presentation.viewmodel.CartViewModel.CartState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
        setupClickListeners()
        setupSwipeRefresh()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCartItems()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadCartItems()
        }
    }

    private fun initRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseClick = { productId, quantity ->
                viewModel.increaseItemQuantity(productId)
            },
            onDecreaseClick = { productId, quantity ->
                if (quantity > 1) {
                    viewModel.decreaseItemQuantity(productId)
                }
            },
            onRemoveClick = { productId ->
                viewModel.removeItemFromCart(productId)
            }
        )
        binding.cartRecyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.localCart.collect { cartItems ->
                // Обновляем список товаров в адаптере
                cartAdapter.setList(cartItems)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSyncing.collect { isSyncing ->
                // Блокируем кнопку оформления заказа во время синхронизации
                binding.bottomBar.root.isEnabled = !isSyncing
                binding.bottomBar.root.alpha = if (isSyncing) 0.5f else 1f
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is CartState.Loading -> showLoading()
                    is CartState.Content -> showContent(state)
                    is CartState.Empty -> showEmptyState()
                    is CartState.Error -> showError(state)
                }
            }
        }
    }

    private fun showLoading() {
        hideAllViews()
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun showContent(state: CartState.Content) {
        hideAllViews()
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            cartRecyclerView.visibility = View.VISIBLE
            bottomBar.root.visibility = View.VISIBLE

            // Обновляем сумму
            bottomBar.totalPriceTextView.text = state.totalPrice.toString()

            // Обновляем список
            (cartRecyclerView.adapter as? CartAdapter)?.setList(state.items)

            bottomBar.checkoutButton.setOnClickListener {
                // TODO: Navigate to checkout fragment
            }
        }
    }

    private fun showEmptyState() {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError(state: CartState.Error) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.errorState.visibility = View.VISIBLE
        binding.errorTextView.text = getString(R.string.error_load)
    }

    private fun hideAllViews() {
        with(binding) {
            progressBar.visibility = View.GONE
            cartRecyclerView.visibility = View.GONE
            emptyState.visibility = View.GONE
            errorState.visibility = View.GONE
            bottomBar.root.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.retryButton.setOnClickListener { viewModel.loadCartItems() }
        binding.goToShopButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_categoryFragment)
        }
    }
}