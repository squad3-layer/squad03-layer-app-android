package com.example.feature.authentication.presentation.register.viewModel

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
class CadastroViewModel @Inject constructor() : ViewModel() {

    private val _resetState = MutableLiveData<Result<Unit>>()
    val resetState: LiveData<Result<Unit>> = _resetState

    private val _loginState = MutableLiveData<Result<Unit>>()
    val loginState: LiveData<Result<Unit>> = _loginState

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
}

