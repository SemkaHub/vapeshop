package com.example.vapeshop.presentation.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentOrderProductsBinding
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.presentation.common.adapter.OrderProductAdapter
import com.example.vapeshop.presentation.common.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderProductsBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var glide: RequestManager

    private val binding by viewBinding(FragmentOrderProductsBinding::bind)
    private lateinit var orderProductAdapter: OrderProductAdapter

    companion object {
        private const val ORDER_PRODUCTS = "orderProducts"

        fun newInstance(orderProducts: List<CartItem>): OrderProductsBottomSheet {
            val fragment = OrderProductsBottomSheet()
            val args = Bundle().apply {
                putParcelableArrayList(ORDER_PRODUCTS, ArrayList(orderProducts))
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProducts()
    }

    private fun showProducts() {
        initRecyclerView()
        val products = getProducts()
        Log.d("OrderProductsBottomSheet", "Products: $products")
        orderProductAdapter.setList(products)
    }

    private fun getProducts(): List<CartItem> {
        return arguments?.getParcelableArrayList<CartItem>(ORDER_PRODUCTS)
            ?: throw Exception("List is null")
    }

    private fun initRecyclerView() {
        val errorDrawable = getDrawable(requireContext(), R.drawable.load_drawable_error)
        orderProductAdapter = OrderProductAdapter(glide, errorDrawable)
        binding.productsRecycler.apply {
            adapter = orderProductAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}