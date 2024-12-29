package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vapeshop.databinding.FragmentShopBinding
import com.example.vapeshop.utils.viewBinding

class ShopFragment : Fragment() {

    private val binding by viewBinding(FragmentShopBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.vapeshop.R.layout.fragment_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewShop.text = "Shop fragment"
    }
}