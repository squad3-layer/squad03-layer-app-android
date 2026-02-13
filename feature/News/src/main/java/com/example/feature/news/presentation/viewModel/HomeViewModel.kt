package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.feature.news.data.paging.NewsPagingSource
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.model.NewsFilters
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.services.analytics.AnalyticsTags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _filters = MutableStateFlow(NewsFilters())
    private val _country = MutableStateFlow("us")

    val articles: Flow<PagingData<Article>> = combine(_filters, _country) { filters, country ->
        Pair(filters, country)
    }.flatMapLatest { (filters, country) ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    getTopHeadlinesUseCase,
                    country,
                    filters.category,
                    filters.shouldReverseOrder
                )
            }
        ).flow
    }.cachedIn(viewModelScope)

    fun applyFilters(filters: NewsFilters) {
        _filters.value = filters
    }

    fun loadTopHeadlines(country: String = "us") {
        _country.value = country
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }

}
