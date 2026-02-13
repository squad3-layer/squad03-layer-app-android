package com.example.feature.news.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase

class NewsPagingSource(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val country: String,
    private val category: String? = null,
    private val shouldReverseOrder: Boolean = false
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val result = getTopHeadlinesUseCase(country, page, category)

            result.fold(
                onSuccess = { newsResponse ->
                    val articles = if (shouldReverseOrder) {
                        newsResponse.articles.reversed()
                    } else {
                        newsResponse.articles
                    }

                    LoadResult.Page(
                        data = articles,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (articles.isEmpty()) null else page + 1
                    )
                },
                onFailure = { exception ->
                    LoadResult.Error(exception)
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

