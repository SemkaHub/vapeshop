package com.example.vapeshop.presentation.map

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vapeshop.domain.model.Address
import com.example.vapeshop.domain.usecase.user.SaveUserAddressUseCase
import com.example.vapeshop.domain.usecase.validation.ValidationAddressUseCase
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MapActivityViewModel @Inject constructor(
    application: Application,
    private val validationAddressUseCase: ValidationAddressUseCase,
    private val saveUserAddressUseCase: SaveUserAddressUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val geocoder = Geocoder(application, Locale.getDefault())
    private var job: Job? = null

    fun getAddress(point: Point) {
        job?.cancel()

        job = CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            _uiState.value = MapUiState.Loading
            try {
                geocoder.getFromLocation(point.latitude, point.longitude, 1)?.firstOrNull()
                    ?.let { address ->
                        if (address.thoroughfare == null || address.locality == null) {
                            _uiState.emit(MapUiState.Error(MapAddressError.INCORRECT_ADDRESS))
                            return@launch
                        }
                        if (address.featureName == null) {
                            _uiState.emit(MapUiState.Error(MapAddressError.NO_APARTMENT))
                            return@launch
                        }
                        val addressDomain = Address(
                            street = address.thoroughfare,
                            city = address.locality,
                            apartment = address.featureName
                        )
                        _uiState.emit(MapUiState.Content(addressDomain))
                    }
            } catch (_: IOException) {
                _uiState.emit(MapUiState.Error(MapAddressError.NO_INTERNET))
            }
        }
    }

    fun saveAddress(addressString: String) {
        _uiState.value = MapUiState.Loading
        if (!validationAddressUseCase(addressString)) {
            _uiState.value = MapUiState.Error(MapAddressError.INCORRECT_ADDRESS)
            return
        }
        val address = Address(
            city = addressString.split(",")[0].trim(),
            street = addressString.split(",")[1].trim(),
            apartment = addressString.split(",")[2].trim()
        )
        viewModelScope.launch {
            try {
                saveUserAddressUseCase(address)
                _uiState.emit(MapUiState.Success)
            } catch (e: Exception) {
                Log.d("MapActivityViewModel", "Error saving address", e)
                _uiState.value = MapUiState.Error(MapAddressError.UNKNOWN_ERROR)
            }
        }
    }
}