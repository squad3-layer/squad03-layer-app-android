package com.example.feature.authentication.data.login.repository


import com.example.feature.authentication.domain.login.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth // Injetado via Hilt
) : LoginRepository {
    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            val task = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (task.user != null) Result.success(Unit)
            else Result.failure(Exception("Usuário nulo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}