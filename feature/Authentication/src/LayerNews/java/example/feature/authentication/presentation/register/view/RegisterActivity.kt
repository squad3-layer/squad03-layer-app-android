package com.example.feature.authentication.presentation.register.view

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.feature.authentication.databinding.ActivityRegisterBinding
import com.example.feature.authentication.presentation.register.viewModel.RegisterViewModel
import androidx.lifecycle.lifecycleScope
import com.domleondev.designsystem.contract.DesignSystem
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.example.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding
    @Inject
    lateinit var navigator: Navigator
    private lateinit var firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics
    @Inject lateinit var uiRenderer: UiRenderer
    @Inject lateinit var designSystem: DesignSystem

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadScreen()

        observeDesignSystemEvents()
        observeViewModel()
    }

    private fun observeViewModel() {

        viewModel.emailError.observe(this) { resId ->
            designSystem.setError("email_input", resId?.let { getString(it) })
        }
        viewModel.cpfError.observe(this) { resId ->
            designSystem.setError("cpf_input", resId?.let { getString(it) })
        }
        viewModel.passwordError.observe(this) { resId ->
            designSystem.setError("password_input", resId?.let { getString(it) })
        }
        viewModel.confirmPasswordError.observe(this) { resId ->
            designSystem.setError("confirm_password_input", resId?.let { getString(it) })
        }

        viewModel.isButtonEnabled.observe(this) { isEnabled ->
            designSystem.setEnabled("button_click_login", isEnabled)
        }

        viewModel.registerState.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }.onFailure { e ->
                showErrorDialog("Alerta", e.message ?: "Erro ao realizar cadastro")
            }
        }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> binding.progressBarLoading.visibility = View.VISIBLE
                is UiState.Success -> {
                    binding.progressBarLoading.visibility = View.GONE
                    uiRenderer.render(binding.containerDs, state.screen)

                    binding.root.post {
                        designSystem.setEnabled("button_click_login", viewModel.isButtonEnabled.value ?: false)
                    }
                }
                is UiState.Error -> {
                    binding.progressBarLoading.visibility = View.GONE
                    showErrorDialog("Alerta", state.message)
                }
            }
        }
    }

    private fun observeDesignSystemEvents() {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                when (event) {
                    is DsUiEvent.Change -> {

                        val name = designSystem.getValue("name_input") ?: ""
                        val email = designSystem.getValue("email_input") ?: ""
                        val cpf = designSystem.getValue("cpf_input") ?: ""
                        val password = designSystem.getValue("password_input") ?: ""
                        val confirmPassword = designSystem.getValue("confirm_password_input") ?: ""

                        viewModel.onInputChangedRegister(name, cpf, email, password, confirmPassword)
                    }
                    is DsUiEvent.Analytics -> {
                        android.util.Log.d("APP_EVENT", "Analytics disparado: ${event.eventName}")
                        val bundle = Bundle().apply {
                            putString("origin", "server_driven_ui")
                        }
                        firebaseAnalytics.logEvent(event.eventName, bundle)
                    }
                    is DsUiEvent.Submit -> {
                        val userRequest = RegisterRequest(
                            username = designSystem.getValue("name_input") ?: "",
                            email = designSystem.getValue("email_input") ?: "",
                            cpf = designSystem.getValue("cpf_input") ?: "",
                            password = designSystem.getValue("password_input") ?: ""
                        )
                        viewModel.register(userRequest)
                        android.util.Log.d("APP_EVENT", "Submit da tela: ${event.screenId}")
                    }
                    is DsUiEvent.Action -> {
                        android.util.Log.d("APP_EVENT", "Action recebida: ${event.action}")
                        if (event.action == "menu:open") {
                        }
                        handleNavigation(event.action)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun handleNavigation(action: String) {
        if (action.startsWith("navigate:")) {
            val destination = action.substringAfter(":")

            when (destination) {
                "login" -> {
                    android.util.Log.d("APP_EVENT", "Sucesso! Saindo da tela...")
                    Toast.makeText(this, "Navegando para o Login...", Toast.LENGTH_SHORT).show()
                }
                "back" -> {
                    finish()
                }
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
}
