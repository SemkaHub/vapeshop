package com.example.vapeshop.presentation.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCheckoutBinding
import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.model.CartItem
import com.example.vapeshop.domain.model.DeliveryMethod
import com.example.vapeshop.domain.model.Order
import com.example.vapeshop.domain.model.PaymentMethod
import com.example.vapeshop.domain.model.PaymentStatus
import com.example.vapeshop.presentation.auth.AuthActivity
import com.example.vapeshop.presentation.cart.CartViewModel
import com.example.vapeshop.presentation.common.adapter.OrderProductAdapter
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: CheckoutViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private val binding by viewBinding(FragmentCheckoutBinding::bind)
    private lateinit var orderProductAdapter: OrderProductAdapter
    private var selectedDeliveryMethod: DeliveryMethod = DeliveryMethod.COURIER
    private var selectedPaymentMethod: PaymentMethod = PaymentMethod.ONLINE

    private var address: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initObservers()
        setupClickListeners()
        setupDeliveryOptions()
        setupPaymentOptions()
        setupPickupPoints()
        setupFragmentResultListener()
    }

    private fun initRecyclerView() {
        val errorDrawable = getDrawable(requireContext(), R.drawable.load_drawable_error)
        orderProductAdapter = OrderProductAdapter(glide, errorDrawable)
        binding.productsRecycler.apply {
            adapter = orderProductAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.localCart.collect { cartItems ->
                // Обновляем список товаров в адаптере
                orderProductAdapter.setList(cartItems)

                updateTotalPrice(cartItems)
            }
        }
    }

    private fun updateTotalPrice(cartItems: List<CartItem>) {
        val total = cartItems.sumOf { it.product.price * it.quantity }
        binding.totalAmount.text = String.format(getString(R.string.total_price), total)
    }

    private fun setupDeliveryOptions() {
        binding.deliveryMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.courierRadio -> {
                    selectedDeliveryMethod = DeliveryMethod.COURIER
                    binding.pickupAddressContainer.visibility = View.GONE
                    binding.deliveryAddressContainer.visibility = View.VISIBLE
                }

                R.id.pickupRadio -> {
                    selectedDeliveryMethod = DeliveryMethod.PICKUP
                    binding.pickupAddressContainer.visibility = View.VISIBLE
                    binding.deliveryAddressContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun setupPaymentOptions() {
        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.cardPayment -> PaymentMethod.ONLINE
                R.id.cashPayment -> PaymentMethod.ON_DELIVERY
                else -> PaymentMethod.ON_DELIVERY
            }
        }
    }

    private fun setupPickupPoints() {
        val pickupPoints = listOf("Пункт 1", "Пункт 2", "Пункт 3")
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, pickupPoints
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.pickupAddressSpinner.adapter = adapter
    }

    private fun validateForm(): Boolean {
        if (cartViewModel.localCart.value.isEmpty()) {
            val emptyCartMessage = getString(R.string.empty_cart)
            Toast.makeText(requireContext(), emptyCartMessage, Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedDeliveryMethod == DeliveryMethod.COURIER &&
            binding.selectedAddressTextView.visibility == View.GONE
        ) {
            val errorMessage = getString(R.string.delivery_address_error)
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createOrder(): Order {
        var cartItems = cartViewModel.localCart.value
        var totalPrice = viewModel.calculateTotalPrice(cartItems)

        return Order(
            items = cartItems,
            totalPrice = totalPrice,
            deliveryMethod = selectedDeliveryMethod,
            paymentMethod = selectedPaymentMethod,
            deliveryAddress = if (selectedDeliveryMethod == DeliveryMethod.COURIER) {
                val addressObject = address?.split(",")?.map { it.trim() }
                Address(
                    city = addressObject?.get(0).toString(),
                    street = addressObject?.get(1).toString(),
                    apartment = addressObject?.get(2).toString()
                )
            } else null,
            pickupPointId = if (selectedDeliveryMethod == DeliveryMethod.PICKUP) {
                binding.pickupAddressSpinner.selectedItem.toString()
            } else null
        )
    }

    private fun showPaymentDialog(order: Order) {
        val title = getString(R.string.online_payment_dialog_title)
        val message = getString(R.string.online_payment_dialog_message)
        val positiveButton = getString(R.string.online_payment_dialog_confirm)
        val negativeButton = getString(R.string.cancel)
        AlertDialog.Builder(requireContext()).setTitle(title)
            .setMessage(String.format(message, order.totalPrice))
            .setPositiveButton(positiveButton) { _, _ ->
                lifecycleScope.launch {
                    completeOrder(order.copy(paymentStatus = PaymentStatus.PAID))
                }
            }.setNegativeButton(negativeButton, null).show()
    }

    private fun completeOrder(order: Order) {
        lifecycleScope.launch {
            try {
                showProcessing(true)
                viewModel.placeOrder(order)
                cartViewModel.clearCart()
                val successMessage = getString(R.string.order_success)
                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } catch (e: Exception) {
                val errorMessage = getString(R.string.order_failed)
                if (e.message == "User not authenticated") showAuthorizationDialog()
                else Toast.makeText(context, errorMessage + e.message, Toast.LENGTH_SHORT).show()
            } finally {
                showProcessing(false)
            }
        }
    }

    private fun showAuthorizationDialog() {
        val title = getString(R.string.authorization_dialog_title)
        val message = getString(R.string.authorization_dialog_message)
        val positiveButton = getString(R.string.authorization_dialog_confirm)
        val negativeButton = getString(R.string.cancel)
        AlertDialog.Builder(requireContext()).setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ ->
                val intent = AuthActivity.newIntent(requireContext())
                startActivity(intent)
            }.setNegativeButton(negativeButton, null).show()
    }

    private fun showProcessing(isProcessing: Boolean) {
        binding.progressBarOverlay.visibility = if (isProcessing) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.placeOrderButton.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            val order = createOrder()

            lifecycleScope.launch {
                try {
                    if (order.paymentMethod == PaymentMethod.ONLINE) {
                        showPaymentDialog(order)
                    } else {
                        completeOrder(order)
                    }
                } catch (e: Exception) {
                    val errorMessage = getString(R.string.order_failed)
                    Toast.makeText(context, errorMessage + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.selectAddressTextView.setOnClickListener {
            val bottomSheet = SelectDeliveryAddressBottomSheet.newInstance()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }
    }

    private fun setupFragmentResultListener() {
        childFragmentManager.setFragmentResultListener(
            SelectDeliveryAddressBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            address = bundle.getString(SelectDeliveryAddressBottomSheet.ADDRESS_KEY)
            binding.selectedAddressTextView.text = address
            binding.selectedAddressTextView.visibility = View.VISIBLE
        }
    }
}