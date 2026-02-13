package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.repository.FavoritesRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(articleUrl: String): Result<Unit> {
        return repository.removeFavorite(articleUrl)
    }
}

