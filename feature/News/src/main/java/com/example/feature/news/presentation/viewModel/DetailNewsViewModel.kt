package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.news.domain.model.Article
import com.example.feature.news.domain.usecase.CheckIsFavoriteUseCase
import com.example.feature.news.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailNewsViewModel @Inject constructor(
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val checkIsFavoriteUseCase: CheckIsFavoriteUseCase
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            toggleFavoriteUseCase(article).fold(
                onSuccess = { newState ->
                    _isFavorite.value = newState
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erro ao atualizar favorito"
                }
            )
        }
    }

    fun checkIfIsFavorite(articleUrl: String) {
        viewModelScope.launch {
            checkIsFavoriteUseCase(articleUrl).fold(
                onSuccess = { isFav ->
                    _isFavorite.value = isFav
                    _error.value = null
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Erro ao verificar favorito"
                    _isFavorite.value = false
                }
            )
        }
    }
}