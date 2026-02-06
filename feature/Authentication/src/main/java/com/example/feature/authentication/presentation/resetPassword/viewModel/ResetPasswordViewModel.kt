package com.example.feature.authentication.presentation.resetPassword.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feature.authentication.R
import com.example.feature.authentication.domain.login.model.AnalyticsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    val analyticsHelper: AnalyticsHelper
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

        _isLoading.value = true
        analyticsHelper.logEvent("reset_password_attempt")

        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val methods = task.result?.signInMethods ?: emptyList<String>()

                if (methods.isNotEmpty()) {
                    performEmailReset(email)
                    analyticsHelper.logEvent("reset_password_user_found")
                } else {

                    _isLoading.value = false
                    _errorMessage.value = "USER_NOT_FOUND"
                    analyticsHelper.logEvent("reset_password_error", mapOf("reason" to "user_not_found"))

                }
            } else {

                _isLoading.value = false
                val exception = task.exception
                _errorMessage.value = exception?.message ?: "Erro desconhecido"


                analyticsHelper.logEvent("reset_password_technical_error")
                exception?.let {
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
            }
        }
    }

    private fun performEmailReset(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            _isLoading.value = false
            if (task.isSuccessful) {
                _resetResult.value = true
            } else {
                _resetResult.value = false
                _errorMessage.value = task.exception?.message ?: "Falha ao enviar e-mail"
            }
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