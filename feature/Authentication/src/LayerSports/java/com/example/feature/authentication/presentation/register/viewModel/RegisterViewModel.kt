package com.example.feature.authentication.presentation.register.viewModel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domleondev.designsystem.domain.repository.RemoteConfigRepository
import com.domleondev.designsystem.domain.usecase.RenderScreenUseCase
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.authentication.R
import com.example.services.analytics.AnalyticsTags
import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.feature.authentication.domain.register.useCase.RegisterUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val useCase: RegisterUseCase,
    val analyticsHelper: AnalyticsTags,
    private val renderScreenUseCase: RenderScreenUseCase,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private val _registerState = MutableLiveData<Result<Unit>>()
    val registerState: LiveData<Result<Unit>> = _registerState

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _emailError = MutableLiveData<Int?>()
    val emailError: LiveData<Int?> = _emailError

    private val _passwordError = MutableLiveData<Int?>()
    val passwordError: LiveData<Int?> = _passwordError

    private val _usernameError = MutableLiveData<Int?>()
    val usernameError: LiveData<Int?> = _usernameError

    private val _cpfError = MutableLiveData<Int?>()
    val cpfError: LiveData<Int?> = _cpfError

    private val _confirmPasswordError = MutableLiveData<Int?>()
    val confirmPasswordError: LiveData<Int?> = _confirmPasswordError

    private val _isButtonEnabled = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isButtonEnabled

    private val _errorState = MutableLiveData<Int?>()
    val errorState: LiveData<Int?> = _errorState


    fun loadScreen() {

        _uiState.value = UiState.Loading

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val jsonString = remoteConfig.getString("register_screen_sports")

                if (jsonString.isNotEmpty()) {
                    try {
                        val screenDefinition = renderScreenUseCase(jsonString)
                        _uiState.value = UiState.Success(screenDefinition!!)
                    } catch (e: Exception) {
                        _uiState.value = UiState.Error("Erro ao processar JSON do Firebase")
                    }
                } else {
                    _uiState.value = UiState.Error("JSON vazio no Remote Config")
                }
            } else {
                _uiState.value = UiState.Error("Falha ao buscar dados do Firebase")
            }
        }
    }

    fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                _emailError.value = R.string.register_Email_error
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _emailError.value = R.string.register_Email_error
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
                _passwordError.value = R.string.register_passsword_required
                false
            }
            password.length < 6 -> {
                _passwordError.value = R.string.register_Password_error
                false
            }
            else -> {
                _passwordError.value = null
                true
            }
        }
    }

    fun validateUsername(userName: String): Boolean {
        return when {
            userName.isEmpty() -> {
                _usernameError.value = R.string.register_username_required
                false
            }
            else -> {
                _usernameError.value = null
                true
            }
        }
    }

    fun validateCpf(cpf: String): Boolean {
        val cleanCpf = cpf.replace(Regex("[^0-9]"), "")

        return when {
            cleanCpf.isEmpty() -> {
                _cpfError.value = R.string.register_cpf_required
                false
            }
            cleanCpf.length != 11 -> {
                _cpfError.value = R.string.register_cpf_must_have_11_digits
                false
            }
            cleanCpf.all { it == cleanCpf[0] } -> {
                _cpfError.value = R.string.register_cpf_invalid
                false
            }
            else -> {
                _cpfError.value = null
                true
            }
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return when {
            confirmPassword.isEmpty() -> {
                _confirmPasswordError.value = R.string.register_confirm_passsword_required
                false
            }
            password != confirmPassword -> {
                _confirmPasswordError.value = R.string.register_confirm_password_mismatch
                false
            }
            else -> {
                _confirmPasswordError.value = null
                true
            }
        }
    }

    fun onInputChangedRegister(
        username: String,
        cpf: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val isUsernameValid = validateUsername(username)
        val isCpfValid = validateCpf(cpf)
        val isEmailValid = validateEmail(email)
        val isPasswordValid = validatePassword(password)
        val isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)

        _isButtonEnabled.value = isUsernameValid && isCpfValid && isEmailValid &&
                isPasswordValid && isConfirmPasswordValid
    }

    fun register(user: RegisterRequest) {
        _errorState.value = null
        viewModelScope.launch {
            _isLoading.value = true
            analyticsHelper.logEvent("register_attempt")

            val result = useCase(user)
            result.onSuccess {
                _registerState.value = result
                analyticsHelper.logEvent("register_success")
            }.onFailure { e ->
                _registerState.value = result
                analyticsHelper.logEvent("register_error", mapOf("reason" to (e.message ?: "unknown")))
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            _isLoading.value = false
        }
    }
}

