package com.example.vapeshop.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentCheckoutBinding
import com.example.vapeshop.presentation.utils.viewBinding

class CheckoutFragment : Fragment() {

    private val viewModel: CheckoutViewModel by viewModels()
    private val binding by viewBinding(FragmentCheckoutBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }
}