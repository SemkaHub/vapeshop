package com.example.vapeshop.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupNavigation()
        setupWindowInsets()
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        // Настройка AppBar
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.categoryFragment,
                R.id.cartFragment,
                R.id.profileFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomNavigationView.setupWithNavController(navController)

        // Обработка выбора пункта меню
        binding.bottomNavigationView.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.categoryFragment -> {
                    navController.popBackStack(R.id.categoryFragment, false)
                }

                R.id.cartFragment -> {
                    navController.popBackStack(R.id.cartFragment, false)
                }

                R.id.profileFragment -> {
                    navController.popBackStack(R.id.profileFragment, false)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Обновляем выбранный пункт в BottomNavigationView
            when (destination.id) {
                R.id.categoryFragment,
                R.id.cartFragment,
                R.id.profileFragment -> {
                    binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
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