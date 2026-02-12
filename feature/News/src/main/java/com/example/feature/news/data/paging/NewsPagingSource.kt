package com.example.feature.news.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.repository.NewsRepository

class NewsPagingSource(
    private val newsRepository: NewsRepository,
    private val country: String
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val result = newsRepository.getTopHeadlines(country, page)

            result.fold(
                onSuccess = { newsResponse ->
                    LoadResult.Page(
                        data = newsResponse.articles,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (newsResponse.articles.isEmpty()) null else page + 1
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

