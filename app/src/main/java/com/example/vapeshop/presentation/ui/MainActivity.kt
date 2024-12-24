package com.example.vapeshop.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.databinding.ActivityMainBinding
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import com.example.vapeshop.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.categories.observe(this) {
            categoryAdapter.setList(it)
        }
    }

    private fun initRecyclerView() {
        categoryAdapter = CategoryAdapter()
        binding.categoriesRecyclerView.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }
    }
}