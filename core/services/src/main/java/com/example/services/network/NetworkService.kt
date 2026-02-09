package com.example.services.network

import kotlinx.coroutines.flow.Flow

interface NetworkService {
    suspend fun <T> get(endpoint: String, clazz: Class<T>, params: Map<String, String>? = null): Result<T>
    suspend fun <T> post(endpoint: String, clazz: Class<T>, body: Map<String, Any>): Result<T>
    suspend fun <T> put(endpoint: String, clazz: Class<T>, body: Map<String, Any>): Result<T>
    suspend fun <T> delete(endpoint: String, clazz: Class<T>): Result<T>
    fun <T> observeGet(endpoint: String, clazz: Class<T>, params: Map<String, String>? = null): Flow<Result<T>>
}

