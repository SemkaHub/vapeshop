package com.example.vapeshop.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vapeshop.databinding.FragmentProfileBinding
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.presentation.viewmodel.ProfileViewModel
import com.example.vapeshop.presentation.viewmodel.ProfileViewModel.ProfileUiState
import com.example.vapeshop.presentation.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    private val viewLifecycleScope by lazy { lifecycleScope }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(com.example.vapeshop.R.layout.fragment_profile, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        initObservers()
    }

    private fun setupUI() {
        with(binding) {
//            signOutButton.setOnClickListener {
//                viewModel.signOut()
//            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getCurrentUser()
            }

            retryButton.setOnClickListener {
                viewModel.getCurrentUser()
            }
        }
    }


    private fun initObservers() {
        viewLifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileUiState.collect { state ->
                    renderState(state)
                }
            }
        }

        viewModel.getCurrentUser()
    }

    private fun renderState(state: ProfileUiState) {
        with(binding) {
            progressBar.isVisible = state is ProfileUiState.Loading
            contentGroup.isVisible = state is ProfileUiState.Success
            errorGroup.isVisible = state is ProfileUiState.Error

            when (state) {
                is ProfileUiState.Loading -> handleLoading()
                is ProfileUiState.Success -> handleSuccess(state.user)
                is ProfileUiState.Error -> handleError(state.message)
                is ProfileUiState.UnAuthorized -> navigateToAuth()
            }
        }
    }

    private fun handleLoading() {
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun handleSuccess(user: User) {
        binding.swipeRefreshLayout.isRefreshing = false
        with(binding) {
            nameTextView.text = user.email
//            emailTextView.text = user.email
//            phoneTextView.text = user.phone
//            signOutButton.isVisible = true
        }
    }

    private fun handleError(message: String) {
        binding.swipeRefreshLayout.isRefreshing = false
        with(binding) {
            errorMessageTextView.text = message
            retryButton.isVisible = true
        }
        showErrorSnackbar(message)
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            viewModel.getCurrentUser()
        }.show()
    }

    private fun navigateToAuth() {
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
