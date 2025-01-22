package com.example.vapeshop.data.usecase.validation

import com.example.vapeshop.domain.model.ValidationResult
import com.example.vapeshop.domain.usecase.validation.ValidateEmailUseCase
import javax.inject.Inject

class ValidateEmailUseCaseImpl @Inject constructor() : ValidateEmailUseCase {
    override fun invoke(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "Invalid email format"
            )

            else -> ValidationResult(
                isValid = true
            )
        }
    }
}