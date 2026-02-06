package com.example.feature.authentication.presentation.resetPassword.view

import android.annotation.SuppressLint
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
import com.example.feature.authentication.R
import com.example.feature.authentication.databinding.ActivityResetPasswordBinding
import com.example.feature.authentication.presentation.resetPassword.viewModel.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private val viewModel: ResetPasswordViewModel by viewModels()

    private lateinit var binding: ActivityResetPasswordBinding

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
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        fun validateCurrentInputs() {
            val email = binding.resetInputFocusEmail.text.toString().trim()
            viewModel.onInputChanged(email)
        }

        binding.resetInputFocusEmail.addTextChangedListener {
            validateCurrentInputs()
        }

        binding.resetButtonClickLogin.setDsClickListener {
            val email = binding.resetInputFocusEmail.text.toString().trim()
            viewModel.sendResetPasswordEmail(email)
        }
    }

    private fun observeViewModel() {

        val progressBar = findViewById<ProgressBar>(R.id.progressBar_loading)

        fun updateButtonState() {
            val isFormValid = viewModel.isButtonEnabled.value ?: false
            val isAppLoading = viewModel.isLoading.value ?: false

            binding.resetButtonClickLogin.isEnabled = isFormValid && !isAppLoading
            binding.resetButtonClickLogin.alpha = if (isFormValid && !isAppLoading) 1.0f else 0.5f
        }

        viewModel.isButtonEnabled.observe(this) { updateButtonState() }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateButtonState()
        }

        viewModel.emailError.observe(this) { resId ->
            binding.resetInputFocusEmail.error = resId?.let { getString(it) }
        }

        viewModel.resetResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, getString(R.string.reset_password_toast_success), Toast.LENGTH_LONG).show()
                finish()
            }
        }
        viewModel.errorMessage.observe(this) { msg ->
            msg?.let {
                if (it == "USER_NOT_FOUND") {
                    showErrorDialog(
                        title = getString(R.string.reset_password_dialog_alert),
                        message = getString(R.string.reset_password_dialog_description)
                    )
                } else {
                    showErrorDialog(getString(R.string.login_dialog_alert), it)
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