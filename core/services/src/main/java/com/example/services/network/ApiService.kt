package com.example.services.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
    @GET("{endpoint}")
    suspend fun get(
        @Path("endpoint") endpoint: String,
        @QueryMap params: Map<String, String>? = null
    ): Map<String, Any>

    @POST("{endpoint}")
    suspend fun post(
        @Path("endpoint") endpoint: String,
        @Body body: Map<String, Any>
    ): Map<String, Any>

    @PUT("{endpoint}")
    suspend fun put(
        @Path("endpoint") endpoint: String,
        @Body body: Map<String, Any>
    ): Map<String, Any>

    @DELETE("{endpoint}")
    suspend fun delete(
        @Path("endpoint") endpoint: String
    ): Map<String, Any>
}
