package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vapeshop.databinding.FragmentCartBinding
import com.example.vapeshop.utils.viewBinding

class CartFragment : Fragment() {

    private val binding by viewBinding(FragmentCartBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.vapeshop.R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewCart.text = "Cart fragment"
    }
}