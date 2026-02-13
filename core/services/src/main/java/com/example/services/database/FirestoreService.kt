package com.example.services.database

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FirestoreService {
    suspend fun saveDocument(collection: String, document: String, data: Map<String, Any>): Result<Unit>
    suspend fun updateDocument(collection: String, document: String, data: Map<String, Any>): Result<Unit>
    suspend fun deleteDocument(collection: String, document: String): Result<Unit>
    suspend fun getDocument(collection: String, document: String): Result<Map<String, Any>>
    suspend fun getCollection(collection: String): Result<List<Map<String, Any>>>
}

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreService {

    override suspend fun saveDocument(
        collection: String,
        document: String,
        data: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(collection)
                .document(document)
                .set(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDocument(
        collection: String,
        document: String,
        data: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(collection)
                .document(document)
                .update(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDocument(collection: String, document: String): Result<Unit> {
        return try {
            firestore.collection(collection)
                .document(document)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDocument(
        collection: String,
        document: String
    ): Result<Map<String, Any>> {
        return try {
            val documentSnapshot = firestore.collection(collection)
                .document(document)
                .get()
                .await()
            Result.success(documentSnapshot.data ?: emptyMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCollection(collection: String): Result<List<Map<String, Any>>> {
        return try {
            val querySnapshot = firestore.collection(collection)
                .get()
                .await()
            val documents = querySnapshot.documents.map { it.data ?: emptyMap() }
            Result.success(documents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
