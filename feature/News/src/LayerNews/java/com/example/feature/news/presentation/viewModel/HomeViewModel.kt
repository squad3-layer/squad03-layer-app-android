package com.example.feature.news.presentation.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.feature.news.data.local.entity.toDomain
import com.example.feature.news.data.paging.NewsRemoteMediator
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.model.NewsFilters
import com.example.feature.news.domain.repository.NewsRepository
import com.example.feature.news.domain.usecase.GetEverythingUseCase
import com.example.services.analytics.AnalyticsTags
import com.example.services.database.local.dao.ArticleDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class, kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEverythingUseCase: GetEverythingUseCase,
    private val newsRepository: NewsRepository,
    private val articleDao: ArticleDao,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _filters = MutableStateFlow(NewsFilters())
    private val _country = MutableStateFlow("us")
    private val _searchQuery = MutableStateFlow("")

    private val _filtersCount = MutableLiveData<Int>()
    val filtersCount = _filtersCount

    val articles: Flow<PagingData<Article>> = combine(
        _filters,
        _searchQuery.debounce(1000L)
    ) { filters, query ->
        Pair(filters, query)
    }.flatMapLatest { (filters, query) ->
        val actualQuery = query.ifBlank { null }
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            remoteMediator = NewsRemoteMediator(
                getEverythingUseCase = getEverythingUseCase,
                articleDao = articleDao,
                category = filters.category,
                query = actualQuery
            ),
            pagingSourceFactory = {
                newsRepository.getCachedArticles(
                    category = filters.category,
                    query = actualQuery,
                    shouldReverseOrder = filters.shouldReverseOrder
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }.cachedIn(viewModelScope)

    fun applyFilters(filters: NewsFilters) {
        Log.d(TAG, "🔍 Aplicando filtros - Category: ${filters.category}, Reverse: ${filters.shouldReverseOrder}")
        _filters.value = filters
    }

    fun onSearchQueryChanged(query: String) {
        Log.d(TAG, "🔎 Busca alterada: $query")
        _searchQuery.value = query
    }

    fun loadTopHeadlines(country: String = "us") {
        Log.d(TAG, "🌍 Carregando notícias para país: $country")
        _country.value = country
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
