package com.example.feature.news.data.repository

import androidx.paging.PagingSource
import com.example.feature.news.domain.model.NewsResponse
import com.example.feature.news.domain.repository.NewsRepository
import com.example.services.database.local.dao.ArticleDao
import com.example.services.database.local.entity.ArticleEntity
import com.example.services.network.NetworkService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val networkService: NetworkService,
    private val articleDao: ArticleDao
) : NewsRepository {

    private val apiKey = "60b253dd44c046dc83105fa3f130b064"

    override suspend fun getEverything(
        page: Int,
        category: String?,
        query: String?,
        language: String
    ): Result<NewsResponse> {
        val qValue = when {
            !query.isNullOrBlank() && !category.isNullOrBlank() -> "${category} ${query}".trim()
            !query.isNullOrBlank() -> query.trim()
            category.isNullOrBlank() && query.isNullOrBlank() -> "general"
            !category.isNullOrBlank() && query.isNullOrBlank() -> category.trim()
            else -> "general"
        }
        val params = mutableMapOf(
            "q" to qValue,
            "language" to language,
            "page" to page.toString(),
            "apiKey" to apiKey
        )
        return networkService.get(
            endpoint = "v2/everything",
            clazz = NewsResponse::class.java,
            params = params
        )
    }

    override fun observeEverything(language: String): Flow<Result<NewsResponse>> {
        val params = mapOf(
            "q" to "general",
            "language" to language,
            "page" to "1",
            "apiKey" to apiKey
        )
        return networkService.observeGet(
            endpoint = "v2/everything",
            clazz = NewsResponse::class.java,
            params = params
        )
    }

    override fun getCachedArticles(
        category: String?,
        query: String?,
        shouldReverseOrder: Boolean
    ): PagingSource<Int, ArticleEntity> {
        return if (shouldReverseOrder) {
            articleDao.getArticlesPagingReversed(category, query)
        } else {
            articleDao.getArticlesPaging(category, query)
        }
    }

    override suspend fun hasCachedData(category: String?, query: String?): Boolean {
        return articleDao.getArticlesByFilter(category, query) != null
    }
}
