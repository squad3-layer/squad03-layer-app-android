package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.model.NewsResponse
import com.example.feature.news.domain.repository.NewsRepository
import javax.inject.Inject

class GetEverythingUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        category: String? = null,
        query: String? = null,
        language: String = "pt"
    ): Result<NewsResponse> {
        return newsRepository.getEverything(page, category, query, language)
    }
}
