package com.example.vapeshop.data.usecase.validation

import com.example.vapeshop.domain.model.ValidationResult
import com.example.vapeshop.domain.usecase.validation.ValidatePasswordUseCase
import javax.inject.Inject

class ValidatePasswordUseCaseImpl @Inject constructor() : ValidatePasswordUseCase {
    override fun invoke(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Password cannot be empty"
            )

            password.length < 6 -> ValidationResult(
                isValid = false,
                errorMessage = "Password must be at least 6 characters"
            )

            else -> ValidationResult(
                isValid = true
            )
        }
    }
}