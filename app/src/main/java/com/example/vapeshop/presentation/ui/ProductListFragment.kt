package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentProductListBinding
import com.example.vapeshop.presentation.adapter.ProductAdapter
import com.example.vapeshop.presentation.viewmodel.ProductViewModel
import com.example.vapeshop.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val binding by viewBinding(FragmentProductListBinding::bind)
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryId = arguments?.getString("categoryId") ?: ""
        initRecyclerView()
        initObservers(categoryId)
    }

    private fun initRecyclerView() {
        productAdapter = ProductAdapter()
        binding.productsRecyclerView.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers(categoryId: String) {
        viewModel.getProducts(categoryId)

        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.setList(products)
        }
    }
}