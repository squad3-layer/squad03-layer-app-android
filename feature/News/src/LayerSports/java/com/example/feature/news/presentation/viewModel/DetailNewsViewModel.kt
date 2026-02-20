package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.news.domain.model.Article
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val renderScreenUseCase: RenderScreenUseCase,
    private val remoteConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private var screenStructure: ScreenDefinition? = null

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()
    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    fun buildScreen(article: Article) {
        _uiState.value = UiState.Loading

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val jsonString = remoteConfig.getString("details_screen")

            try {
                val screenDefinition = renderScreenUseCase(jsonString)

                val dynamicComponents = screenDefinition?.components?.map { component ->
                    when (component.id) {
                        "news_image" -> component.copy(props = component.props?.plus("imageUrl" to article.urlToImage.orEmpty()))
                        "news_title" -> component.copy(props = component.props?.plus("title" to article.title))
                        "news_author" -> component.copy(props = component.props?.plus("title" to (article.author ?: "Autor desconhecido")))
                        "news_content" -> component.copy(props = component.props?.plus("title" to (article.description ?: "")))
                        else -> component
                    }
                }

                _uiState.value = UiState.Success(ScreenDefinition("details", dynamicComponents ?: emptyList()))
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Erro ao montar detalhes")
            }
        }
    }
    fun toggleFavorite(article: Article) {
        val userId = auth.currentUser?.uid ?: return
        val newState = !_isFavorite.value
        _isFavorite.value = newState

        val documentId = article.url.hashCode().toString()
        val docRef = firestore.collection("users").document(userId)
            .collection("favorites").document(documentId)

        if (newState) {
            docRef.set(article)
                .addOnFailureListener { _isFavorite.value = false }
        } else {
            docRef.delete()
                .addOnFailureListener { _isFavorite.value = true }
        }
    }
}