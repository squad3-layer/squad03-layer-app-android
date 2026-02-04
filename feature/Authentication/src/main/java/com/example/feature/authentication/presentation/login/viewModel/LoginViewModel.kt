package com.example.feature.authentication.presentation.login.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.authentication.R
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


    private val _emailError = MutableLiveData<Int?>()
    val emailError: LiveData<Int?> = _emailError

    private val _passwordError = MutableLiveData<Int?>()
    val passwordError: LiveData<Int?> = _passwordError

    private val _isButtonEnabled = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            analyticsHelper.logEvent("login_attempt")

            val result = loginUseCase(email, password)
            result.onSuccess {
                _loginState.value = result
                analyticsHelper.logEvent("login_success")
            }.onFailure { e ->
                _loginState.value = result
                analyticsHelper.logEvent("login_error", mapOf("reason" to (e.message ?: "unknown")))
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
                 _emailError.value = R.string.login_toast_input_email
                 false
             }
             else -> {
                 _emailError.value = null
                 true
             }
         }
    }

    fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                _passwordError.value = R.string.login_field_required_password
                false
            }
            password.length < 6 -> {
                _passwordError.value = R.string.login_toast_input_password
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }
    fun onInputChanged(email: String, password: String) {
        val isEmailValid = validateEmail(email)
        val isPasswordFieldValid = validatePassword(password)

        _isButtonEnabled.value = isEmailValid && isPasswordFieldValid
    }
}