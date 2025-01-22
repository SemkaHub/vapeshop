package com.example.vapeshop.di

import com.example.vapeshop.data.usecase.validation.ValidateEmailUseCaseImpl
import com.example.vapeshop.data.usecase.validation.ValidatePasswordUseCaseImpl
import com.example.vapeshop.domain.usecase.validation.ValidateEmailUseCase
import com.example.vapeshop.domain.usecase.validation.ValidatePasswordUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ValidationModule {
    @Binds
    abstract fun bindEmailValidator(
        validateEmailUseCaseImpl: ValidateEmailUseCaseImpl
    ): ValidateEmailUseCase

    @Binds
    abstract fun bindPasswordValidator(
        validatePasswordUseCaseImpl: ValidatePasswordUseCaseImpl
    ): ValidatePasswordUseCase
}