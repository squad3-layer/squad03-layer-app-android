package com.example.feature.authentication.data.register.repository

import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.feature.authentication.domain.register.repository.RegisterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class RegisterRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : RegisterRepository {

    override suspend fun signUp(user: RegisterRequest): Result<Unit> {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(user.email, user.password)
                .await()

            val uid = authResult.user?.uid
                ?: throw IllegalStateException("Ocorreu um erro ao recuperar o UID.")

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to user.email,
                "name" to user.username,
                "cpf" to user.cpf
            )

            firestore.collection("users")
                .document(uid)
                .set(userMap)
                .await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}