package com.example.feature.authentication.domain.register.useCase

import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.feature.authentication.domain.register.repository.RegisterRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(user: RegisterRequest) =
        repository.signUp(user)
}