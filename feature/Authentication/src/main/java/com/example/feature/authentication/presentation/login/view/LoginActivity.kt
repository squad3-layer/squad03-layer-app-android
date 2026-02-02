package com.example.feature.authentication.presentation.login.view

import android.R.attr.password
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels // Delegado para o ViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityLoginBinding
import com.example.feature.authentication.presentation.MainActivity
import com.example.feature.authentication.presentation.login.viewModel.LoginViewModel
import com.example.mylibrary.ds.button.DsButton
import com.example.mylibrary.ds.input.DsInput
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.analyticsHelper.logScreenView("screen_view_login")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_view_login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {

        fun validateCurrentInputs() {
            val email = binding.inputFocusEmail.text.toString()
            val password = binding.inputFocusPassword.text.toString()
            viewModel.onInputChanged(email, password)
        }

        binding.inputFocusEmail.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.inputFocusPassword.addTextChangedListener {
            validateCurrentInputs()
        }
        binding.buttonClickLogin.setDsClickListener {

            val email = binding.inputFocusEmail.text.toString().trim()
            val password = binding.inputFocusPassword.text.toString()

            viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "login_button"))
            viewModel.login(email, password)
        }
    }

    private fun observeViewModel() {

        val progressBar = findViewById<ProgressBar>(R.id.progressBar_loading)

        fun updateButtonState() {
            val isFormValid = viewModel.isButtonEnabled.value ?: false
            val isAppLoading = viewModel.isLoading.value ?: false

            binding.buttonClickLogin.isEnabled = isFormValid && !isAppLoading
            binding.buttonClickLogin.alpha = if (isFormValid && !isAppLoading) 1.0f else 0.5f
        }

        viewModel.isButtonEnabled.observe(this) { updateButtonState() }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateButtonState()
        }

        viewModel.emailError.observe(this) { resId ->
            binding.inputFocusEmail.error = resId?.let { getString(it) }
        }

        viewModel.passwordError.observe(this) { resId ->
            binding.inputFocusPassword.error = resId?.let { getString(it) }
        }

        viewModel.loginState.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { exception ->
                showErrorDialog()
            }
        }
    }
    private fun showErrorDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.login_dialog_alert))
            .setMessage(getString(R.string.login_dialog_authentication_fail))
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}