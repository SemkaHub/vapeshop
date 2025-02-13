package com.example.vapeshop.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupNavigation()
    }


    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.post {
            val layoutParams =
                binding.fragmentContainer.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.bottomMargin = binding.bottomNavigationView.height
            binding.fragmentContainer.layoutParams = layoutParams
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, right = systemBars.right, left = systemBars.left)
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        // Обработка выбора пункта меню
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.categoryFragment -> {
                    navController.navigate(R.id.categoryFragment)
                    true
                }

                R.id.cartFragment -> {
                    navController.navigate(R.id.cartFragment)
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Обновляем выбранный пункт в BottomNavigationView
            when (destination.id) {
                R.id.categoryFragment,
                R.id.cartFragment -> {
                    binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
                    binding.toolbar.isVisible = false
                }

                R.id.checkoutFragment -> {
                    binding.toolbar.isVisible = false
                }

                R.id.ordersFragment,
                R.id.profileSettingsFragment -> {
                    binding.bottomNavigationView.menu.findItem(R.id.profileFragment)?.isChecked =
                        true
                    binding.toolbar.isVisible = false
                }

                R.id.profileFragment -> {
                    binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
                    binding.toolbar.isVisible = false
                }

                R.id.productListFragment -> {
                    binding.bottomNavigationView.menu.findItem(R.id.categoryFragment)?.isChecked =
                        true
                    binding.toolbar.isVisible = false
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!navController.popBackStack()) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}