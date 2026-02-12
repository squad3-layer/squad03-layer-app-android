package com.example.feature.news.domain.usecase

import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {
    operator fun invoke(): Flow<Result<List<Article>>> {
        return repository.getFavorites()
    }
}

