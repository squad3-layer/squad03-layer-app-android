package com.example.feature.authentication.presentation.register.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityRegisterBinding
import com.example.feature.authentication.presentation.login.view.LoginActivity
import com.example.feature.authentication.presentation.register.viewModel.RegisterViewModel
import androidx.core.widget.addTextChangedListener
import com.example.feature.authentication.domain.register.model.RegisterRequest
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupWindowInsets()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setBackButton(show = true) {
                viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "register_back_button"))
                finish()
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        binding.inputEmail.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.inputUsuario.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.inputCpf.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.inputSenha.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.inputConfirmarSenha.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.registerButton.setDsClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val username = binding.inputUsuario.text.toString().trim()
            val cpf = binding.inputCpf.text.toString().trim()
            val password = binding.inputSenha.text.toString().trim()
            val user = RegisterRequest(
                username = username,
                email = email,
                cpf = cpf,
                password = password
            )

            viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "register_button"))
            viewModel.register(user)

        }
    }


    private fun validateCurrentInputs() {
        val email = binding.inputEmail.text.toString().trim()
        val username = binding.inputUsuario.text.toString().trim()
        val cpf = binding.inputCpf.text.toString().trim()
        val password = binding.inputSenha.text.toString().trim()
        val confirmPassword = binding.inputConfirmarSenha.text.toString().trim()
        viewModel.onInputChangedRegister(username, cpf, email, password, confirmPassword)
    }

    private fun observeViewModel() {
        fun updateButtonState() {

            val isButtonEnabled = viewModel.isButtonEnabled.value ?: false
            val isAppLoading = viewModel.isLoading.value ?: false

            binding.registerButton.isEnabled = isButtonEnabled && !isAppLoading
            binding.registerButton.alpha = if (isButtonEnabled && !isAppLoading) 1.0f else 0.5f
        }

        viewModel.isButtonEnabled.observe(this) { updateButtonState() }

        viewModel.usernameError.observe(this) { resId ->
            binding.inputUsuario.error = resId?.let { getString(it) }
        }

        viewModel.emailError.observe(this) { resId ->
            binding.inputEmail.error = resId?.let { getString(it) }
        }

        viewModel.cpfError.observe(this) { resId ->
            binding.inputCpf.error = resId?.let { getString(it) }
        }

        viewModel.passwordError.observe(this) { resId ->
            binding.inputSenha.error = resId?.let { getString(it) }
        }

        viewModel.confirmPasswordError.observe(this) { resId ->
            binding.inputConfirmarSenha.error = resId?.let { getString(it) }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateButtonState()
        }

        viewModel.registerState.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.onFailure  { exception ->
                val messageId = when (exception) {
                    is FirebaseAuthUserCollisionException -> R.string.register_email_already_in_use
                    else -> R.string.register_generic_error
                }

                showErrorDialog(getString(messageId))

            }
        }
    }

    private fun showErrorDialog(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.register_dialog_alert))
            .setMessage(message)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
