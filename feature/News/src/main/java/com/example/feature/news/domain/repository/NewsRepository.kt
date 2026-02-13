package com.example.feature.news.domain.repository

import androidx.paging.PagingSource
import com.example.feature.news.domain.model.NewsResponse
import com.example.services.database.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(
        country: String = "us",
        page: Int = 1,
        category: String? = null,
        query: String? = null
    ): Result<NewsResponse>

    fun observeTopHeadlines(country: String = "us"): Flow<Result<NewsResponse>>

    fun getCachedArticles(
        category: String?,
        query: String?,
        shouldReverseOrder: Boolean = false
    ): PagingSource<Int, ArticleEntity>

    suspend fun hasCachedData(category: String?, query: String?): Boolean
}
