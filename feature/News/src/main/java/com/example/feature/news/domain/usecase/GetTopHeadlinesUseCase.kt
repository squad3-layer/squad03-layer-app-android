package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.model.NewsResponse
import com.example.feature.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetTopHeadlinesUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(
        country: String = "us",
        page: Int = 1,
        category: String? = null
    ): Result<NewsResponse> {
        return newsRepository.getTopHeadlines(country, page, category)
    }
}
