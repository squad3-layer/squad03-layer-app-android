package com.example.feature.authentication.domain.register.repository

import com.example.feature.authentication.domain.register.model.RegisterRequest

interface RegisterRepository {
     suspend fun signUp (user: RegisterRequest) : Result<Unit>
}