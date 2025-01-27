package com.example.vapeshop.domain.usecase.validation

import android.util.Patterns
import com.example.vapeshop.domain.model.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Email cannot be empty"
            )

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "Invalid email format"
            )

            else -> ValidationResult(
                isValid = true
            )
        }
    }
}