package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vapeshop.databinding.FragmentCartBinding
import com.example.vapeshop.presentation.adapter.CartAdapter
import com.example.vapeshop.presentation.viewmodel.CartViewModel
import com.example.vapeshop.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.vapeshop.R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
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
        viewModel.cartItems.observe(viewLifecycleOwner) {
            cartAdapter.setList(it)
        }

        viewModel.totalPrice.observe(viewLifecycleOwner) { it ->
            binding.totalPriceTextView.text = it.toString()
        }
    }

}