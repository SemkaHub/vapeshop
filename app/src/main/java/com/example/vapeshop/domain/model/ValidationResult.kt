package com.example.vapeshop.domain.model

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
