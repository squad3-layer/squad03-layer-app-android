package com.example.feature.news.data.favorites

import com.example.feature.news.domain.model.Article
import com.example.feature.news.data.remote.FavoritesRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val remoteDataSource: FavoritesRemoteDataSource
) : com.example.feature.news.domain.repository.FavoritesRepository {

    override fun getFavorites(): Flow<Result<List<Article>>> = flow {
        emit(remoteDataSource.getFavorites())
    }

    override suspend fun addFavorite(article: Article): Result<Unit> {
        return remoteDataSource.addFavorite(article)
    }

    override suspend fun removeFavorite(articleUrl: String): Result<Unit> {
        return remoteDataSource.removeFavorite(articleUrl)
    }

    override suspend fun isFavorite(articleUrl: String): Result<Boolean> {
        return remoteDataSource.isFavorite(articleUrl)
    }
}

