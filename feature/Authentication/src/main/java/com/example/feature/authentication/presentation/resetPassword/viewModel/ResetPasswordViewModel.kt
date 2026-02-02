package com.example.feature.authentication.presentation.resetPassword.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.authentication.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _resetState = MutableLiveData<Result<Unit>>()
    val resetState: LiveData<Result<Unit>> = _resetState

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

            delay(2000)
            _resetState.value = Result.success(Unit)
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