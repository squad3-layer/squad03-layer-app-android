package com.example.feature.authentication.domain.register.model

data class RegisterRequest (
    val username: String,
    val email: String,
    val cpf: String,
    val password: String
)