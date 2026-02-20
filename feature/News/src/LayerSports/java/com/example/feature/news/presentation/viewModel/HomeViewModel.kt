package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.domleondev.designsystem.domain.model.Component
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.domleondev.designsystem.presentation.state.UiState
import com.example.services.analytics.AnalyticsTags
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    val analyticsHelper: AnalyticsTags,
    private val renderScreenUseCase: RenderScreenUseCase,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private var screenStructure: ScreenDefinition? = null
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private var _articlesList: List<com.example.feature.news.domain.model.Article> = emptyList()
    val articlesList: List<com.example.feature.news.domain.model.Article> get() = _articlesList
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        fetchRemoteHomeScreen()
    }

    fun fetchRemoteHomeScreen() {
        _uiState.value = UiState.Loading

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val jsonString = remoteConfig.getString("home_screen")

                if (jsonString.isNotEmpty()) {
                    try {
                        screenStructure = renderScreenUseCase(jsonString)
                        loadTopHeadlines()
                    } catch (e: Exception) {
                        _uiState.value = UiState.Error("Erro ao processar estrutura da tela")
                    }
                }
            }
        }
    }

    fun loadTopHeadlines() {
        viewModelScope.launch {
            val result = getTopHeadlinesUseCase("us")

            result.onSuccess { newsResponse ->
                _articlesList = newsResponse.articles
                val newsComponents = newsResponse.articles.map { article ->
                    Component(
                        id = article.url,
                        type = "NewsCard",
                        props = mapOf(
                            "title" to (article.title ?: ""),
                            "description" to (article.description ?: ""),
                            "imageUrl" to (article.urlToImage ?: ""),
                            "date" to (article.publishedAt ?: ""),
                            "action" to "navigate:details"
                        )
                    )
                }

                val finalComponents = mutableListOf<Component>()
                screenStructure?.components?.let { componentsFromJson ->
                    val structureOnly = componentsFromJson.filter { it.type != "NewsCard" }
                    finalComponents.addAll(structureOnly)
                }

                finalComponents.addAll(newsComponents)

                _uiState.value = UiState.Success(ScreenDefinition(screenName = "home", components = finalComponents))

            }.onFailure {
                _uiState.value = UiState.Error("Erro ao carregar notícias")
            }
        }
    }

    fun logNotificationClick() {
        analyticsHelper.logEvent("notifications_icon_click")
    }

    fun logFavoriteClick() {
        analyticsHelper.logEvent("favorites_icon_click")
    }

}
