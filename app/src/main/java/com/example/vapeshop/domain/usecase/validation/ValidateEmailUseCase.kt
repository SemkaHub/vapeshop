package com.example.vapeshop.domain.usecase.validation

import com.example.vapeshop.domain.model.ValidationResult

interface ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationResult
}