package com.example.feature.news.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.domleondev.designsystem.presentation.state.UiState
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val renderScreenUseCase: RenderScreenUseCase,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private var screenStructure: ScreenDefinition? = null

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    fun fetchRemoteFiltersScreen() {
        _uiState.value = UiState.Loading

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val jsonString = remoteConfig.getString("filters_screen")

                if (jsonString.isNotEmpty()) {
                    try {
                        val screen = renderScreenUseCase(jsonString)
                        screenStructure = screen

                        screen?.let {
                            _uiState.value = UiState.Success(it)
                        } ?: run {
                            _uiState.value = UiState.Error("Estrutura da tela vazia")
                        }
                    } catch (e: Exception) {
                        _uiState.value = UiState.Error("Erro ao processar estrutura da tela")
                    }
                }
            }
        }
    }
}