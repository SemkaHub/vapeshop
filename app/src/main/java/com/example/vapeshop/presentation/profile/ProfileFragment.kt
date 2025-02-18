package com.example.vapeshop.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentProfileBinding
import com.example.vapeshop.domain.model.User
import com.example.vapeshop.presentation.auth.AuthActivity
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var glide: RequestManager

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем профиль при возврате на экран
        viewModel.getCurrentUserFromLocal()
    }

    private fun setupOnClickListeners() {
        with(binding) {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.getUserProfileFromServer()
            }

            retryButton.setOnClickListener {
                viewModel.getUserProfileFromServer()
            }

            ordersButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
            }

            settingsButton.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_profileSettingsFragment)
            }

            logoutButton.setOnClickListener {
                showConfirmationDialog()
            }

            goToAuthButton.setOnClickListener {
                val intent = AuthActivity.newIntent(requireContext())
                startActivity(intent)
            }
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.sign_out_title)
            .setMessage(R.string.sign_out_message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                viewModel.signOut()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileUiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: ProfileUiState) {
        with(binding) {
            contentGroup.isVisible = state is ProfileUiState.Success
            errorGroup.isVisible = state is ProfileUiState.Error
            unAuthorizedGroup.isVisible = state is ProfileUiState.UnAuthorized

            // Отключить SwipeRefreshLayout для неавторизованного состояния
            swipeRefreshLayout.isEnabled = !unAuthorizedGroup.isVisible

            when (state) {
                is ProfileUiState.Loading -> showLoading()
                is ProfileUiState.Success -> showSuccess(state.user)
                is ProfileUiState.Error -> showError(state.message)
                is ProfileUiState.UnAuthorized -> showUnAuthorized()
            }
        }
    }

    private fun showLoading() {
        if (!binding.swipeRefreshLayout.isRefreshing) {
            binding.progressBar.isVisible = true
        }
    }

    private fun showSuccess(user: User) {
        with(binding) {
            hideLoadingIndicator()
            emailTextView.text = user.email
            nameTextView.text = user.name
        }
    }

    private fun showError(message: String) {
        with(binding) {
            hideLoadingIndicator()
            errorMessageTextView.text = message
            retryButton.isVisible = true
        }
    }

    private fun showUnAuthorized() {
        hideLoadingIndicator()
        binding.unAuthorizedGroup.isVisible = true
    }

    private fun hideLoadingIndicator() {
        binding.progressBar.isVisible = false
        binding.swipeRefreshLayout.isRefreshing = false
    }
}
