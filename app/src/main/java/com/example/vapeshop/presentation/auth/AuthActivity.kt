package com.example.vapeshop.presentation.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityAuthBinding
import com.example.vapeshop.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        with(binding) {
            loginButton.setOnClickListener {
                handleLogin()
            }

            registerButton.setOnClickListener {
                handleRegister()
            }

            emailEditText.addTextChangedListener {
                emailTextInputLayout.error = null
                emailTextInputLayout.isErrorEnabled = false
            }

            passwordEditText.addTextChangedListener {
                passwordTextInputLayout.error = null
                passwordTextInputLayout.isErrorEnabled = false
            }
        }
    }

    private fun setupObservers() {
        viewModel.authUiState.observe(this, ::handleAuthState)
    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Initial -> resetUiState()
            is AuthState.Loading -> showLoading()
            is AuthState.Success -> handleSuccess()
            is AuthState.Error -> handleError(state)
        }
    }

    private fun resetUiState() {
        with(binding) {
            progressBar.visibility = View.GONE
            setButtonsEnabled(true)
            emailTextInputLayout.apply {
                error = null
                isErrorEnabled = false
            }
            passwordTextInputLayout.apply {
                error = null
                isErrorEnabled = false
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        setButtonsEnabled(false)
    }

    private fun handleSuccess() {
        binding.progressBar.visibility = View.GONE
        setButtonsEnabled(true)
        navigateToMain()
    }

    private fun handleError(handleError: AuthState.Error) {
        binding.progressBar.visibility = View.GONE
        setButtonsEnabled(true)

        handleError.message?.let { errorType ->
            val errorMessage = getErrorMessage(errorType)
            if (errorMessage != null) {
                showToast(errorMessage)
            }
        }

        with(binding) {
            emailTextInputLayout.apply {
                error = handleError.emailError?.let { getEmailErrorMessage(it) }
                isErrorEnabled = handleError.emailError != null
            }

            passwordTextInputLayout.apply {
                error = handleError.passwordError?.let { getPasswordErrorMessage(it) }
                isErrorEnabled = handleError.passwordError != null
            }
        }
    }

    private fun getErrorMessage(errorType: AuthErrorType): String? = when (errorType) {
        AuthErrorType.GENERIC_ERROR -> getString(R.string.error_default)
        AuthErrorType.NETWORK_ERROR -> getString(R.string.error_network)
        AuthErrorType.INVALID_CREDENTIALS -> getString(R.string.error_invalid_credentials)
        AuthErrorType.USER_NOT_FOUND -> getString(R.string.error_user_not_found)
        AuthErrorType.SAVE_USER_ERROR -> getString(R.string.error_save_user)
        else -> null
    }

    private fun getEmailErrorMessage(errorType: AuthErrorType): String? = when (errorType) {
        AuthErrorType.INVALID_EMAIL -> getString(R.string.error_invalid_email)
        else -> null
    }

    private fun getPasswordErrorMessage(errorType: AuthErrorType): String? = when (errorType) {
        AuthErrorType.PASSWORD_SHORT -> getString(R.string.error_password_short)
        else -> null
    }

    private fun handleLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        viewModel.loginUser(email, password)
    }

    private fun handleRegister() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        viewModel.registerUser(email, password)
    }

    private fun navigateToMain() {
        startActivity(MainActivity.newIntent(this))
        finish()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        with(binding) {
            loginButton.isEnabled = enabled
            registerButton.isEnabled = enabled
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}