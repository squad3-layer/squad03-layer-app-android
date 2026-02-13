package com.example.feature.news.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.feature.news.data.local.entity.toEntity
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.services.database.local.dao.ArticleDao
import com.example.services.database.local.entity.ArticleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val articleDao: ArticleDao,
    private val country: String,
    private val category: String?,
    private val query: String?
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "🔄 REFRESH - Buscando dados da API (página 1)")
                    1
                }
                LoadType.PREPEND -> {
                    Log.d(TAG, "⬆️ PREPEND - Ignorado")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        Log.d(TAG, "⬇️ APPEND - Sem último item, voltando para página 1")
                        1
                    } else {
                        val nextPage = (state.pages.size) + 1
                        Log.d(TAG, "⬇️ APPEND - Buscando próxima página: $nextPage")
                        nextPage
                    }
                }
            }

            Log.d(TAG, "📡 Chamando API - Página: $page, Category: $category, Query: $query")

            val result = withContext(Dispatchers.IO) {
                getTopHeadlinesUseCase(country, page, category, query)
            }

            result.fold(
                onSuccess = { newsResponse ->
                    Log.d(TAG, "✅ API retornou ${newsResponse.articles.size} artigos")

                    if (loadType == LoadType.REFRESH) {
                        Log.d(TAG, "🗑️ Limpando apenas artigos antigos (>7 dias)")
                        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
                        articleDao.deleteOldArticles(sevenDaysAgo)
                    }

                    val entities = newsResponse.articles.map { article ->
                        article.toEntity(category, query)
                    }

                    articleDao.insertArticles(entities)
                    Log.d(TAG, "💾 ${entities.size} artigos salvos no cache (INSERT com REPLACE)")

                    MediatorResult.Success(
                        endOfPaginationReached = newsResponse.articles.isEmpty()
                    )
                },
                onFailure = { exception ->
                    Log.e(TAG, "❌ Erro ao buscar da API: ${exception.message}", exception)
                    MediatorResult.Error(exception)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erro no RemoteMediator: ${e.message}", e)
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "NewsRemoteMediator"
    }
}

