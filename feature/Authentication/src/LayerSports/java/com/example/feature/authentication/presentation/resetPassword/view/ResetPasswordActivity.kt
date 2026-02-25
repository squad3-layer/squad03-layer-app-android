package com.example.feature.authentication.presentation.resetPassword.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.domleondev.designsystem.contract.DesignSystem
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityResetPasswordBinding
import com.example.feature.authentication.presentation.login.view.LoginActivity
import com.example.feature.authentication.presentation.resetPassword.viewModel.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private val viewModel: ResetPasswordViewModel by viewModels()

    private lateinit var binding: ActivityResetPasswordBinding

    @Inject
    lateinit var uiRenderer: UiRenderer

    @Inject
    lateinit var designSystem: DesignSystem

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.fetchRemoteResetPasswordScreen()
        observeViewModel()
        observeDesignSystemEvents()
    }

    private fun observeViewModel() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar_loading)

        viewModel.isButtonEnabled.observe(this) { isEnabled ->
            val isLoading = viewModel.isLoading.value ?: false
            designSystem.setEnabled("reset_button_click_login", isEnabled && !isLoading)
        }
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> progressBar.visibility = View.VISIBLE
                is UiState.Success -> {
                    progressBar.visibility = View.GONE
                    designSystem.clear()

                    binding.containerDs.removeAllViews()
                    uiRenderer.render(binding.containerDs, state.screen)
                }

                is UiState.Error -> {
                    progressBar.visibility = View.GONE
                    showErrorDialog("Erro", state.message)
                }
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            val isEnabled = viewModel.isButtonEnabled.value ?: false
            designSystem.setEnabled("reset_button_click_login", isEnabled && !isLoading)
        }

            viewModel.emailError.observe(this) { errorResId ->
                val errorMessage = errorResId?.let { getString(it) }
                designSystem.setError("reset_input_focus_email", errorMessage)
        }
        viewModel.resetResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "E-mail de recuperação enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { messageResId ->
            if (messageResId != null && messageResId != 0) {
                val messageText = getString(messageResId)

                showErrorDialog("Erro no Envio", messageText)
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun observeDesignSystemEvents() {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                when (event) {
                    is DsUiEvent.Change -> {
                        val email = designSystem.getValue("reset_input_focus_email") ?: ""
                        viewModel.onInputChanged(email)
                        }
                    is DsUiEvent.Submit -> {
                        val email = designSystem.getValue("reset_input_focus_email") ?: ""
                        viewModel.sendResetPasswordEmail(email)
                    }
                    is DsUiEvent.Action -> handleNavigation(event.action)
                    is DsUiEvent.Analytics -> viewModel.analyticsHelper.logEvent(event.eventName)
                    else -> {}
                }
            }
        }
    }

    private fun handleNavigation(action: String) {
        val destination = action.substringAfter(":")
        when (destination) {
            "back" -> finish()
        }
    }

}