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

    private val apiKey = "d62add1a990448d0a0ecd45320bdfc82"

    override suspend fun getTopHeadlines(
        country: String,
        page: Int,
        category: String?,
        query: String?
    ): Result<NewsResponse> {
        val params = mutableMapOf(
            "country" to country,
            "page" to page.toString(),
            "apiKey" to apiKey
        )

        category?.let {
            params["category"] = it
        }

        query?.let {
            if (it.isNotBlank()) {
                params["q"] = it
            }
        }

        return networkService.get(
            endpoint = "v2/top-headlines",
            clazz = NewsResponse::class.java,
            params = params
        )
    }

    override fun observeTopHeadlines(country: String): Flow<Result<NewsResponse>> {
        return networkService.observeGet(
            endpoint = "v2/top-headlines",
            clazz = NewsResponse::class.java,
            params = mapOf(
                "country" to country,
                "page" to "1",
                "apiKey" to apiKey
            )
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
