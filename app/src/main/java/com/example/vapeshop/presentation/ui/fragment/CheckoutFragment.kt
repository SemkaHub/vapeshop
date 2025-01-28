package com.example.vapeshop.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCheckoutBinding
import com.example.vapeshop.domain.model.DeliveryMethod
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.model.PaymentMethod
import com.example.vapeshop.presentation.adapter.CheckoutAdapter
import com.example.vapeshop.presentation.utils.viewBinding
import com.example.vapeshop.presentation.viewmodel.CartViewModel
import com.example.vapeshop.presentation.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val binding by viewBinding(FragmentCheckoutBinding::bind)
    private lateinit var checkoutAdapter: CheckoutAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initObservers()
        setupClickListeners()
    }

    private fun initRecyclerView() {
        checkoutAdapter = CheckoutAdapter()
        binding.productsRecycler.apply {
            adapter = checkoutAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.localCart.collect { cartItems ->
                // Обновляем список товаров в адаптере
                checkoutAdapter.setList(cartItems)

                // Рассчитываем общую сумму
                val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
                binding.totalAmount.text =
                    String.format(getString(R.string.total_price), totalPrice)
            }
        }
    }


    private fun setupClickListeners() {
        val deliveryMethodErrorMessage = getString(R.string.delivery_method_error)
        val paymentMethodErrorMessage = getString(R.string.payment_method_error)
        binding.placeOrderButton.setOnClickListener {
            // Получаем выбранные параметры
            val deliveryMethod = when (binding.deliveryMethodGroup.checkedRadioButtonId) {
                R.id.courierRadio -> DeliveryMethod.COURIER
                R.id.pickupRadio -> DeliveryMethod.PICKUP
                else -> {
                    Toast.makeText(context, deliveryMethodErrorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val paymentMethod = when (binding.paymentMethodGroup.checkedRadioButtonId) {
                R.id.cardPayment -> PaymentMethod.ONLINE
                R.id.cashPayment -> PaymentMethod.ON_DELIVERY
                else -> {
                    Toast.makeText(context, paymentMethodErrorMessage, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val currentCart = cartViewModel.localCart.value
            val total = currentCart.sumOf { it.product.price * it.quantity }

            val order = Order(
                items = currentCart,
                totalPrice = total,
                deliveryMethod = deliveryMethod,
                paymentMethod = paymentMethod,
                deliveryAddress = null, // TODO: Реализовать выбор адреса
                pickupPointId = null // TODO: Реализовать выбор пункта
            )

            // TODO: Сохраняем заказ в базе данных
            Toast.makeText(context, "Заказ сохранен", Toast.LENGTH_SHORT).show()

            // Очищаем корзину после успешного сохранения
            cartViewModel.clearCart()
            findNavController().navigateUp()
        }
    }
}