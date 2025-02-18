package com.example.vapeshop.presentation.profilesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.vapeshop.R
import com.example.vapeshop.databinding.FragmentProfileSettingsBinding
import com.example.vapeshop.domain.model.UserProfile
import com.example.vapeshop.presentation.common.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {

    private val viewModel: ProfileSettingsViewModel by viewModels()
    private val binding by viewBinding(FragmentProfileSettingsBinding::bind)

    private lateinit var userProfile: UserProfile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserProfile()
    }

    private fun setupUI() {
        with(binding) {
            addressEditText.keyListener = null
            addressEditText.setOnClickListener {
                findNavController().navigate(R.id.action_profileSettingsFragment_to_mapActivity)
            }

            saveButton.setOnClickListener {
                val firstName = firstNameEditText.text.toString()
                val lastName = lastNameEditText.text.toString()
                val phone = phoneEditText.text.toString()
                val profile = UserProfile(
                    firstName, lastName, phone, userProfile.profilePhotoUrl, userProfile.address
                )
                viewModel.updateProfile(profile)
            }

            profilePhotoImageView.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.profile_photo_click),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileSettingsUiState.collect { state ->
                    when (state) {
                        ProfileSettingsUiState.Initial -> return@collect
                        ProfileSettingsUiState.Loading -> showLoading()
                        ProfileSettingsUiState.Success -> showSuccessMessage()
                        is ProfileSettingsUiState.Content -> showContent(state.userProfile)
                        is ProfileSettingsUiState.Error -> showErrorMessage(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        with(binding) {
            progressBar.visibility = View.VISIBLE
            setFieldsEnabled(false)
        }
    }

    private fun showSuccessMessage() {
        findNavController().navigateUp()
        Toast.makeText(
            requireContext(),
            getString(R.string.profile_settings_success),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showContent(userProfile: UserProfile) {
        with(binding) {
            progressBar.visibility = View.GONE
            setFieldsEnabled(true)
            firstNameEditText.setText(userProfile.firstName)
            lastNameEditText.setText(userProfile.lastName)
            phoneEditText.setText(userProfile.phoneNumber)
            val address = userProfile.address
            if (address != null) {
                val addressString = "${address.city}, ${address.street}, ${address.apartment}"
                addressEditText.setText(addressString)
            }
            this@ProfileSettingsFragment.userProfile = userProfile
        }
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        setFieldsEnabled(true)
    }

    private fun setFieldsEnabled(isEnabled: Boolean) {
        with(binding) {
            profilePhotoImageView.isEnabled = isEnabled
            firstNameEditText.isEnabled = isEnabled
            lastNameEditText.isEnabled = isEnabled
            phoneEditText.isEnabled = isEnabled
        }
    }
}