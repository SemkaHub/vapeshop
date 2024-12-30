package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityMainBinding
import com.example.vapeshop.presentation.adapter.CategoryAdapter
import com.example.vapeshop.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

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

        setupBottomNavigationView()
        setupRecyclerView()
        initObservers()
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_shop -> {
                    replaceFragment(ShopFragment())
                    true
                }

                R.id.menu_cart -> {
                    replaceFragment(CartFragment())
                    true
                }

                R.id.menu_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun setupRecyclerView() {
        val spacing =
            (16 * resources.displayMetrics.density).roundToInt()   // Отступы между карточками
        val spanCount = calculateSpanCount(spacing)            // Вычисление количества колонок
        val cardWidth = calculateCardWidth(spanCount, spacing)  // Вычисление ширины карточки

        initRecyclerView(spanCount, spacing, cardWidth)
    }

    private fun calculateSpanCount(spacing: Int): Int {
        // Нужна реальная ширина экрана. Метод resources.displayMetrics.widthPixels не подходит, так
        // как не учитывает место, зарезервиванное системой, из-за чего отступы у карточек будут разными.
        val screenWidth = windowManager.currentWindowMetrics.bounds.width()
        var spanCount = 1
        val range =
            (150 * resources.displayMetrics.density).roundToInt()..
                    (300 * resources.displayMetrics.density).roundToInt()

        // if card width in 150..300 in dp
        for (i in 2..6) {
            if (screenWidth / i - spacing * (i + 1) in range) spanCount = i
            Log.d(
                "spanCount",
                "spanCount: $spanCount ; screenWidth: $screenWidth ; (${screenWidth / i - spacing * (i + 1)}) ; spacing: $spacing ; (${screenWidth / i}) (${spacing * (i + 1)})"
            )
        }
        return spanCount
    }

    private fun calculateCardWidth(spanCount: Int, spacing: Int): Int {
        val screenWidth = windowManager.currentWindowMetrics.bounds.width()
        val totalSpacing = (spanCount + 1) * spacing
        return ((screenWidth - totalSpacing) / spanCount.toFloat()).roundToInt()
    }

    private fun initObservers() {
        viewModel.categories.observe(this) {
            categoryAdapter.setList(it)
        }
    }

    private fun initRecyclerView(spanCount: Int, spacing: Int, cardWidth: Int) {
        binding.categoriesRecyclerView.apply {
            categoryAdapter = CategoryAdapter(cardWidth) { categoryId ->
                openProductsByCategory(categoryId)
            }
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, spanCount)
            addItemDecoration(CategoryAdapter.MyItemDecoration(spacing))
        }
    }

    private fun openProductsByCategory(categoryId: String) {
        val productListFragment = ProductListFragment().apply {
            arguments = Bundle().apply {
                putString("categoryId", categoryId)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, productListFragment)
            .addToBackStack(null)
            .commit()
    }
}