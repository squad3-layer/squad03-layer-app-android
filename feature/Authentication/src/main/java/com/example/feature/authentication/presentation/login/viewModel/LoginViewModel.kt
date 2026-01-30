package com.example.feature.authentication.presentation.login.viewModel

import android.widget.Toast
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
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

    private val _isValid = MutableLiveData<Boolean>()
    val isValid: LiveData<Boolean> = _isValid

    private val _emailError = MutableLiveData<Int?>()
    val emailError: LiveData<Int?> = _emailError

    private val _passwordError = MutableLiveData<Int?>()
    val passwordError: LiveData<Int?> = _passwordError


    fun login(email: String, password: String) {

        _emailError.value = null
        _passwordError.value = null

        if (!validateEmail(email) || !validatePassword(password)) return

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
        var isValid = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailError.value = R.string.login_toast_input_email
            isValid = false
        }
        return isValid
    }

    fun validatePassword(password: String): Boolean {
        var isValid = true
        if (password.length < 6) {
            _passwordError.value = R.string.login_toast_input_password
            isValid = false
        }
        return isValid
    }

}