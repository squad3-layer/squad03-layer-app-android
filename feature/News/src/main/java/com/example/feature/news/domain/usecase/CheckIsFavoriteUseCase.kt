package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.repository.FavoritesRepository
import javax.inject.Inject

class CheckIsFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    suspend operator fun invoke(articleUrl: String): Result<Boolean> {
        return repository.isFavorite(articleUrl)
    }
}

