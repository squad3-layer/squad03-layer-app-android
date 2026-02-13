package com.example.feature.authentication.data.register.repository

import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.feature.authentication.domain.register.repository.RegisterRepository
import com.example.services.authentication.AuthenticationService
import com.example.services.database.FirestoreService
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject


class RegisterRepositoryImpl @Inject constructor(
    private val authService: AuthenticationService,
    private val firestoreService: FirestoreService,
    private val firebaseAuth: FirebaseAuth
) : RegisterRepository {

    override suspend fun signUp(user: RegisterRequest): Result<Unit> {
        return try {
            authService.signUp(user.email, user.password).getOrThrow()
            val uid = firebaseAuth.currentUser?.uid
                ?: throw IllegalStateException("Ocorreu um erro ao recuperar o UID.")

            val userMap = mapOf(
                "uid" to uid,
                "email" to user.email,
                "name" to user.username,
                "cpf" to user.cpf
            )

            firestoreService.saveDocument("users", uid, userMap).getOrThrow()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}