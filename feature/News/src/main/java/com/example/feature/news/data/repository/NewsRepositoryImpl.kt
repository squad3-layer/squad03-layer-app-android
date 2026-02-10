package com.example.feature.news.data.repository

import com.example.feature.news.data.remote.NewsApiService
import com.example.feature.news.data.remote.toDomain
import com.example.feature.news.domain.model.News
import com.example.feature.news.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val apiKey: String
) : NewsRepository {

    override suspend fun getTopHeadlines(): List<News> {
        val response = api.getEverything(query = "technology", apiKey = apiKey)
        println("Total artigos recebidos: ${response.articles.size}")
        return response.articles.map { it.toDomain() }
    }
}
