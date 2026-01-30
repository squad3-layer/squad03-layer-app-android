package com.example.feature.authentication.domain.login.useCase

import com.example.feature.authentication.domain.login.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repository.signIn(email, password)
}