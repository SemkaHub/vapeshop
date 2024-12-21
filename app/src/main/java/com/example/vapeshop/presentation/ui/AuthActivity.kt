package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vapeshop.databinding.ActivityAuthBinding
import com.example.vapeshop.presentation.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.loginUser(email, password)
        }

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.registerUser(email, password)
        }

        viewModel.authUiState.observe(this) {
            when (it) {
                is AuthViewModel.AuthUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    setButtonsEnabled(false)
                }

                is AuthViewModel.AuthUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                }

                is AuthViewModel.AuthUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.loginButton.isEnabled = enabled
        binding.registerButton.isEnabled = enabled
    }
}