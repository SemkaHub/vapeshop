package com.example.vapeshop.presentation.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentSelectDeliveryAddressBinding
import com.example.vapeshop.presentation.common.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectDeliveryAddressBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentSelectDeliveryAddressBinding::bind)
    private val viewModel: CheckoutViewModel by viewModels()

    companion object {
        const val REQUEST_KEY = "select_address_request_key"
        const val ADDRESS_KEY = "address_key"

        fun newInstance(): SelectDeliveryAddressBottomSheet {
            val fragment = SelectDeliveryAddressBottomSheet()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_select_delivery_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showCurrentAddress()
        setupListeners()
    }

    private fun setupListeners() {
        binding.addNewAddressLayout.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_mapActivity)
        }
        binding.currentAddressLayout.setOnClickListener {
            val currentAddress = binding.currentAddressTextView.text.toString()
            sendResult(currentAddress)
            dismiss()
        }
    }

    private fun showCurrentAddress() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val address = viewModel.getUserAddress()
                if (address != null) {
                    val addressString = "${address.city}, ${address.street}, ${address.apartment}"
                    binding.currentAddressTextView.text = addressString
                    binding.currentAddressLayout.isVisible = true
                } else {
                    binding.currentAddressLayout.isVisible = false
                }
            }
        }
    }

    private fun sendResult(address: String) {
        val bundle = bundleOf(ADDRESS_KEY to address)
        parentFragmentManager.setFragmentResult(REQUEST_KEY, bundle)
    }
}