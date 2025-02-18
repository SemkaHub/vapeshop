package com.example.vapeshop.domain.usecase.validation

import javax.inject.Inject

class ValidationAddressUseCase @Inject constructor() {
    operator fun invoke(addressString: String): Boolean {
        val addressParts = addressString.split(",").map { it.trim() }
        if (addressParts.size != 3) {
            return false
        }

        val city = addressParts[0]
        val street = addressParts[1]
        val apartment = addressParts[2]
        if (city.isEmpty() || street.isEmpty() || apartment.isEmpty()) {
            return false
        }

        val apartmentNumberRegex = Regex("^\\d+(?:[a-zA-Zа-яА-Я]/\\d+)?$")
        return apartment.matches(apartmentNumberRegex)
    }
}