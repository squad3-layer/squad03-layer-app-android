package com.example.feature.authentication.presentation.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.authentication.domain.login.model.AnalyticsHelper
import com.example.feature.authentication.domain.login.useCase.LoginUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    val analyticsHelper: AnalyticsHelper
) : ViewModel() {

    private val _loginState = MutableLiveData<Result<Unit>>()
    val loginState: LiveData<Result<Unit>> = _loginState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            analyticsHelper.logEvent("login_attempt")
            val result = loginUseCase(email, password)
            result.onSuccess {
                _loginState.value = result
                // Tagueamento: Sucesso
                analyticsHelper.logEvent("login_success")
            }.onFailure { e ->
                _loginState.value = result
                // Tagueamento: Erro
                analyticsHelper.logEvent("login_error", mapOf("reason" to (e.message ?: "unknown")))

                // Crashlytics: Registrar o erro sem travar o app (Non-fatal)
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            _isLoading.value = false
        }
    }
}