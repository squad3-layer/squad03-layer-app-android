package com.example.feature.news.data.repository

import com.example.feature.news.domain.model.NewsResponse
import com.example.feature.news.domain.repository.NewsRepository
import com.example.services.network.NetworkService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val networkService: NetworkService
) : NewsRepository {

    private val apiKey = "60b253dd44c046dc83105fa3f130b064"

    override suspend fun getTopHeadlines(country: String, page: Int): Result<NewsResponse> {
        return networkService.get(
            endpoint = "v2/top-headlines",
            clazz = NewsResponse::class.java,
            params = mapOf(
                "country" to country,
                "page" to page.toString(),
                "apiKey" to apiKey
            )
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
}
