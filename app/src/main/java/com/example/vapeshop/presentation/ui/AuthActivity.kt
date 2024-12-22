package com.example.vapeshop.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityAuthBinding
import com.example.vapeshop.presentation.viewmodel.AuthViewModel
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
            login()
        }

        binding.registerButton.setOnClickListener {
            register()
        }

        viewModel.authUiState.observe(this) {
            when (it) {
                is AuthViewModel.AuthUiState.Initial -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                    binding.emailTextInputLayout.error = null
                    binding.emailTextInputLayout.isErrorEnabled = false
                    binding.passwordTextInputLayout.error = null
                    binding.passwordTextInputLayout.isErrorEnabled = false
                }

                is AuthViewModel.AuthUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    setButtonsEnabled(false)
                }

                is AuthViewModel.AuthUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)
                    // Navigate to shop activity
                }

                is AuthViewModel.AuthUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    setButtonsEnabled(true)

                    if (it.message != null) {
                        val errorMessage = when (it.message) {
                            AuthViewModel.AuthErrorType.GENERIC_ERROR -> getString(R.string.error_default)
                            else -> null
                        }
                        if (errorMessage != null) {
                            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }

                    binding.emailTextInputLayout.error = when (it.emailError) {
                        AuthViewModel.AuthErrorType.INVALID_EMAIL -> getString(R.string.error_invalid_email)
                        else -> null
                    }
                    binding.emailTextInputLayout.isErrorEnabled = it.emailError != null

                    binding.passwordTextInputLayout.error = when (it.passwordError) {
                        AuthViewModel.AuthErrorType.PASSWORD_SHORT -> getString(R.string.error_password_short)
                        else -> null
                    }
                    binding.passwordTextInputLayout.isErrorEnabled = it.passwordError != null
                }
            }
        }
    }

    private fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.loginUser(email, password)
    }

    private fun register() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        viewModel.registerUser(email, password)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.loginButton.isEnabled = enabled
        binding.registerButton.isEnabled = enabled
    }
}