package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.designsystem.domain.model.Component
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.news.domain.usecase.GetTopHeadlinesUseCase
import com.example.services.analytics.AnalyticsTags
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
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

                val jsonString = remoteConfig.getString("favorite_screen")

                if (jsonString.isNotEmpty()) {
                    try {
                        screenStructure = renderScreenUseCase(jsonString)
                        loadFavorites()
                    } catch (e: Exception) {
                        _uiState.value = UiState.Error("Erro ao processar estrutura da tela")
                    }
                }
            }
        }
    }

    fun loadFavorites() {
        val userId = auth.currentUser?.uid ?: return
        _uiState.value = UiState.Loading

        firestore.collection("users").document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->

                val articles = result.toObjects(com.example.feature.news.domain.model.Article::class.java)
                val finalComponents = mutableListOf<Component>()
                _articlesList = articles

                screenStructure?.components?.let { componentsFromJson ->
                    val structureOnly = componentsFromJson.filter { it.type != "NewsCard" }
                    finalComponents.addAll(structureOnly)
                }

                if (articles.isEmpty()) {
                    finalComponents.add(
                        Component(
                            id = "empty_state",
                            type = "Text",
                            props = mapOf(
                                "title" to "Você ainda não salvou nenhuma notícia.\nToque no coração em uma notícia para favoritá-la!",
                                "textColor" to "#6B7280",
                                "size" to 16,
                                "gravity" to "center",
                                "margin_top" to 100,
                                "margin_left" to 32,
                                "margin_right" to 32
                            )
                        )
                    )
                } else {

                    val newsComponents = articles.map { article ->
                        Component(
                            id = article.url,
                            type = "NewsCard",
                            props = mapOf(
                                "title" to article.title,
                                "description" to (article.description ?: ""),
                                "imageUrl" to (article.urlToImage ?: ""),
                                "date" to (article.publishedAt ?: ""),
                                "action" to "navigate:details"
                            )
                        )
                    }
                    finalComponents.addAll(newsComponents)
                }

                _uiState.value = UiState.Success(ScreenDefinition(screenName = "favorites", components = finalComponents))
            }
            .addOnFailureListener {
                _uiState.value = UiState.Error("Erro ao carregar favoritos")
            }
    }

}