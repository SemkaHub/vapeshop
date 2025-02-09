package com.example.vapeshop.presentation.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCartBinding
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel: CartViewModel by activityViewModels()
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
        val errorDrawable = getDrawable(requireContext(), R.drawable.load_drawable_error)
        cartAdapter = CartAdapter(glide, errorDrawable,
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
                binding.bottomBar.checkoutButton.isEnabled = !isSyncing
                binding.bottomBar.root.alpha = if (isSyncing) 0.5f else 1f
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is CartUiState.Loading -> showLoading()
                    is CartUiState.Content -> showContent(state)
                    is CartUiState.Empty -> showEmptyState()
                    is CartUiState.Error -> showError(state)
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

    private fun showContent(state: CartUiState.Content) {
        hideAllViews()
        binding.apply {
            swipeRefreshLayout.isRefreshing = false
            cartRecyclerView.visibility = View.VISIBLE
            bottomBar.root.visibility = View.VISIBLE

            // Обновляем сумму
            bottomBar.totalPriceTextView.text =
                String.format(Locale.getDefault(), "%.2f", state.totalPrice)

            // Обновляем список
            cartAdapter.setList(state.items)

            bottomBar.checkoutButton.setOnClickListener {
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
            }
        }
    }

    private fun showEmptyState() {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun showError(state: CartUiState.Error) {
        hideAllViews()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.errorState.visibility = View.VISIBLE
        binding.retryButton.setOnClickListener { state.retryAction() }
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
        binding.goToShopButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_categoryFragment)
        }
    }
}