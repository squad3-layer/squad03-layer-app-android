package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.model.Article
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase
) {
    suspend operator fun invoke(article: Article): Result<Boolean> {
        return checkIsFavoriteUseCase(article.url).fold(
            onSuccess = { isFavorite ->
                if (isFavorite) {
                    removeFavoriteUseCase(article.url).map { false }
                } else {
                    addFavoriteUseCase(article).map { true }
                }
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}

