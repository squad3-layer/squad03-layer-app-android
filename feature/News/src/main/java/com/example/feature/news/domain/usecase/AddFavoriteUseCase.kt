package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.repository.FavoritesRepository
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(article: Article): Result<Unit> {
        return repository.addFavorite(article)
    }
}

