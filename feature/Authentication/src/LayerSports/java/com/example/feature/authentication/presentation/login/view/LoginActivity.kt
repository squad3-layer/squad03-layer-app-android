package com.example.feature.authentication.presentation.login.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.domleondev.designsystem.contract.DesignSystem
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityLoginBinding
import com.example.feature.authentication.presentation.login.viewModel.LoginViewModel
import com.example.feature.authentication.presentation.resetPassword.view.ResetPasswordActivity
import com.example.feature.authentication.presentation.register.view.RegisterActivity
import com.example.navigation.Navigator
import com.example.navigation.routes.NavigationRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    @Inject
    lateinit var navigator: Navigator
    @Inject lateinit var uiRenderer: UiRenderer
    @Inject lateinit var designSystem: DesignSystem

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.fetchRemoteLoginScreen()
        viewModel.analyticsHelper.logScreenView("screen_view_login")


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_view_login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeViewModel()
        observeDesignSystemEvents()

    }
    override fun onResume() {
        super.onResume()
        viewModel.fetchRemoteLoginScreen()
    }
        private fun observeDesignSystemEvents() {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                when (event) {
                    is DsUiEvent.Change -> {

                        val email = designSystem.getValue("email_input") ?: ""
                        val password = designSystem.getValue("password_input") ?: ""

                        viewModel.onInputChanged(email, password)
                    }
                    is DsUiEvent.Submit -> {
                        val email = designSystem.getValue("email_input") ?: ""
                        val password = designSystem.getValue("password_input") ?: ""

                        android.util.Log.d("LOGIN_DEBUG", "Tentando login com: Email: '|$email|', Senha: '|$password|'")

                        viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "login_button"))
                        viewModel.login(email, password)
                    }

                    is DsUiEvent.Action -> handleNavigation(event.action)
                    is DsUiEvent.Analytics -> viewModel.analyticsHelper.logEvent(event.eventName)
                    else -> {}
                }
            }
        }
    }
    private fun observeViewModel() {
        fun updateLoginButtonState() {
            val isFormValid = viewModel.isButtonEnabled.value ?: false
            val isAppLoading = viewModel.isLoading.value ?: false

            val canClick = isFormValid && !isAppLoading

            designSystem.setEnabled("button_click_login", canClick)
        }
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBarLoading.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBarLoading.visibility = View.GONE
                    designSystem.clear()

                    binding.containerDs.removeAllViews()
                    uiRenderer.render(binding.containerDs, state.screen)
                }
                is UiState.Error -> {
                    binding.progressBarLoading.visibility = View.GONE
                    showErrorDialog()
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateLoginButtonState()
        }

        viewModel.isButtonEnabled.observe(this) {
            updateLoginButtonState()
        }

        viewModel.emailError.observe(this) { resId ->
            designSystem.setError("email_input", resId?.let { getString(it) })
        }

        viewModel.passwordError.observe(this) { resId ->
            designSystem.setError("password_input", resId?.let { getString(it) })
        }

        viewModel.loginState.observe(this) { result ->
            result.onSuccess {
                navigator.navigateToActivity(this, NavigationRoute.Home)
                finish()
            }.onFailure {
                showErrorDialog()
            }
        }
    }
    private fun handleNavigation(action: String) {
        val type = action.substringBefore(":")
        val destination = action.substringAfter(":")
        if (type == "navigate") {
            when (destination) {
                "register" -> startActivity(Intent(this, RegisterActivity::class.java))
                "forgot_password" -> startActivity(Intent(this, ResetPasswordActivity::class.java))
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