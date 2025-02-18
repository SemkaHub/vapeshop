package com.example.vapeshop.presentation.map

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vapeshop.R
import com.example.vapeshop.databinding.ActivityMapBinding
import com.example.vapeshop.domain.model.Address
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapActivity : AppCompatActivity() {

    private val viewModel: MapActivityViewModel by viewModels()
    private lateinit var binding: ActivityMapBinding

    private val startLocation = Point(53.189231, 158.382659)
    private val zoom = 15f
    private val smooth = 3f
    val cameraListener: CameraListener = object : CameraListener {
        override fun onCameraPositionChanged(
            map: Map,
            position: CameraPosition,
            reason: CameraUpdateReason,
            finished: Boolean,
        ) {
            if (finished) {
                Log.d("currentposition", "Current : ${position.target}")
                viewModel.getAddress(position.target)
            }
        }
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupMap()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun setupMap() {
        moveToStartLocation()
        setupListeners()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.addressInputLayout.error = null
                    binding.saveButton.isEnabled = state is MapUiState.Content
                    when (state) {
                        is MapUiState.Loading -> showLoading()
                        is MapUiState.Content -> showContent(state.address)
                        is MapUiState.Error -> showError(state.error)
                        is MapUiState.Success -> finish()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.bottomCard.isEnabled = false
        binding.bottomCard.alpha = 0.5f
    }

    private fun showContent(address: Address) {
        with(binding) {
            bottomCard.isEnabled = true
            bottomCard.alpha = 1f
            val addressString = "${address.city}, ${address.street}, ${address.apartment}"
            addressEditText.setText(addressString)
        }
    }

    private fun showError(error: MapAddressError) {
        when (error) {
            MapAddressError.NO_INTERNET -> showInternetError()
            MapAddressError.NO_APARTMENT -> showNoApartmentError()
            MapAddressError.INCORRECT_ADDRESS -> showIncorrectAddressError()
            MapAddressError.UNKNOWN_ERROR -> showUnknownError()
        }
    }

    private fun showUnknownError() {
        val errorMessage = getString(R.string.error_default)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showNoApartmentError() {
        binding.bottomCard.isEnabled = true
        binding.bottomCard.alpha = 1f
        val errorMessage = getString(R.string.no_apartment_error)
        binding.addressInputLayout.setError(errorMessage)
    }

    private fun showIncorrectAddressError() {
        binding.bottomCard.isEnabled = false
        binding.bottomCard.alpha = 1f
        val errorMessage = getString(R.string.incorrect_address_error)
        binding.addressInputLayout.setError(errorMessage)
    }

    private fun showInternetError() {
        binding.bottomCard.isEnabled = false
        binding.bottomCard.alpha = 0.5f
        val errorMessage = getString(R.string.error_network)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setupListeners() {
        binding.mapView.mapWindow.map.addCameraListener(cameraListener)
        binding.saveButton.setOnClickListener {
            val addressString = binding.addressEditText.text.toString()
            viewModel.saveAddress(addressString)
        }

        binding.addressEditText.addTextChangedListener {
            binding.addressInputLayout.error = null
            binding.addressInputLayout.isErrorEnabled = false
            binding.saveButton.isEnabled = true
        }
    }

    private fun moveToStartLocation() {
        binding.mapView.mapWindow.map.move(
            CameraPosition(startLocation, zoom, 0f, 0f),
            Animation(Animation.Type.SMOOTH, smooth),
            null
        )
    }
}