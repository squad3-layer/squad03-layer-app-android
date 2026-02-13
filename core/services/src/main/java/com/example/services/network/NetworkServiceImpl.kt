package com.example.services.network

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NetworkServiceImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
) : NetworkService {

    override suspend fun <T> get(
        endpoint: String,
        clazz: Class<T>,
        params: Map<String, String>?
    ): Result<T> {
        return try {
            val response = apiService.get(endpoint, params)
            val result = gson.fromJson(gson.toJson(response), clazz)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun <T> post(
        endpoint: String,
        clazz: Class<T>,
        body: Map<String, Any>
    ): Result<T> {
        return try {
            val response = apiService.post(endpoint, body)
            val result = gson.fromJson(gson.toJson(response), clazz)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun <T> put(
        endpoint: String,
        clazz: Class<T>,
        body: Map<String, Any>
    ): Result<T> {
        return try {
            val response = apiService.put(endpoint, body)
            val result = gson.fromJson(gson.toJson(response), clazz)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun <T> delete(
        endpoint: String,
        clazz: Class<T>
    ): Result<T> {
        return try {
            val response = apiService.delete(endpoint)
            val result = gson.fromJson(gson.toJson(response), clazz)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun <T> observeGet(
        endpoint: String,
        clazz: Class<T>,
        params: Map<String, String>?
    ): Flow<Result<T>> = flow {
        try {
            val response = apiService.get(endpoint, params)
            val result = gson.fromJson(gson.toJson(response), clazz)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
