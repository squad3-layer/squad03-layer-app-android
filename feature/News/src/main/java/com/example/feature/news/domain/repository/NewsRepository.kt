package com.example.feature.news.domain.repository

import com.example.feature.news.domain.model.NewsResponse
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(
        country: String = "us",
        page: Int = 1,
        category: String? = null,
        query: String? = null
    ): Result<NewsResponse>

    fun observeTopHeadlines(country: String = "us"): Flow<Result<NewsResponse>>
}
