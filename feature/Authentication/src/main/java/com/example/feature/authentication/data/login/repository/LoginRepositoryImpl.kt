package com.example.feature.authentication.data.login.repository

import com.example.feature.authentication.domain.login.repository.LoginRepository
import com.example.services.authentication.AuthenticationService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val authService: AuthenticationService
) : LoginRepository {
    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return authService.signIn(email, password)
    }
}