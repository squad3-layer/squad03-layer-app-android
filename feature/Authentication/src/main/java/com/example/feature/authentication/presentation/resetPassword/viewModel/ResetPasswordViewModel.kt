package com.example.feature.authentication.presentation.resetPassword.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.authentication.R
import com.example.services.analytics.AnalyticsTags
import com.example.services.authentication.AuthenticationService
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authService: AuthenticationService,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _resetState = MutableLiveData<Result<Unit>>()
    val resetState: LiveData<Result<Unit>> = _resetState

    private val _resetResult = MutableLiveData<Boolean>()
    val resetResult: LiveData<Boolean> = _resetResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _emailError = MutableLiveData<Int?>()
    val emailError: LiveData<Int?> = _emailError

    private val _isButtonEnabled = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

    fun sendResetPasswordEmail(email: String) {
        if (!validateEmail(email)) return

        viewModelScope.launch {
            _isLoading.value = true
            analyticsHelper.logEvent("reset_password_attempt")

            val result = authService.sendPasswordResetEmail(email)
            result.onSuccess {
                _resetResult.value = true
                _resetState.value = Result.success(Unit)
                analyticsHelper.logEvent("reset_password_success")
            }.onFailure { e ->
                _resetResult.value = false
                _errorMessage.value = e.message ?: "Falha ao enviar e-mail"
                analyticsHelper.logEvent("reset_password_error", mapOf("reason" to (e.message ?: "unknown")))
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            _isLoading.value = false
        }
    }

    fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                _emailError.value = R.string.login_field_required_email
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _emailError.value = R.string.reset_password_toast_input_email
                false
            }

            else -> {
                _emailError.value = null
                true
            }
        }
    }

    fun onInputChanged(email: String) {
        _isButtonEnabled.value = validateEmail(email)
    }
}