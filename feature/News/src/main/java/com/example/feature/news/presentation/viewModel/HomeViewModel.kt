package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.feature.news.data.paging.NewsPagingSource
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.repository.NewsRepository
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private var currentCountry = "us"

    val articles: Flow<PagingData<Article>> = createPager(currentCountry)

    private fun createPager(country: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                NewsPagingSource(newsRepository, country)
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun loadTopHeadlines(country: String = "us") {
        currentCountry = country
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }

}
