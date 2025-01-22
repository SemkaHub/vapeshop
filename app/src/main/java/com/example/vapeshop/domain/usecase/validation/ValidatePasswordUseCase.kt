package com.example.vapeshop.domain.usecase.validation

import com.example.vapeshop.domain.model.ValidationResult

interface ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult
}