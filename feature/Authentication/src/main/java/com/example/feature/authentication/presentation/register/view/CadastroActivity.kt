package com.example.feature.authentication.presentation.register.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityCadastroBinding
import com.example.feature.authentication.presentation.login.view.LoginActivity
import com.example.feature.authentication.presentation.register.viewModel.CadastroViewModel
import androidx.core.widget.addTextChangedListener

class CadastroActivity : AppCompatActivity() {

    private val viewModel: CadastroViewModel by viewModels()
    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
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
           // to do chamar firebase
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
            binding.registerButton.isEnabled = isButtonEnabled
            binding.registerButton.alpha = if(isButtonEnabled) 1.0f else 0.5f
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

        viewModel.loginState.observe(this) { result ->
            result.onSuccess {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }.onFailure {
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
