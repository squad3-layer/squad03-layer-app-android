package com.example.feature.authentication.domain.login.repository

interface LoginRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
}