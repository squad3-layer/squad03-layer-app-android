package com.example.feature.news.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines")
    suspend fun getTopHeadlines(

        @Query("apiKey") apiKey: String
    ): NewsResponse

    //  Novo endpoint sem filtro de país
    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String,       // palavra-chave (ex: "technology")
        @Query("apiKey") apiKey: String
    ): NewsResponse
}
