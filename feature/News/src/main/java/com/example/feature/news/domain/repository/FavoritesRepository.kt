package com.example.feature.news.domain.repository

import com.example.feature.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<Result<List<Article>>>
    suspend fun addFavorite(article: Article): Result<Unit>
    suspend fun removeFavorite(articleUrl: String): Result<Unit>
    suspend fun isFavorite(articleUrl: String): Result<Boolean>
}

