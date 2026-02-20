package com.example.feature.news.presentation.view

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.domleondev.designsystem.contract.DesignSystem
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityFiltersBinding
import com.example.feature.news.presentation.viewModel.FiltersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FiltersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFiltersBinding

    private val viewModel: FiltersViewModel by viewModels()

    @Inject
    lateinit var uiRenderer: UiRenderer
    @Inject
    lateinit var designSystem: DesignSystem

    private lateinit var firebaseAnalytics: com.google.firebase.analytics.FirebaseAnalytics

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.fetchRemoteFiltersScreen()

        observeViewModel()
        observeDesignSystemEvents()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE

                    val screen = state.screen
                    uiRenderer.render(binding.containerLayout, screen)
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun observeDesignSystemEvents() {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                when (event) {
                    is DsUiEvent.Analytics -> {
                        android.util.Log.d("APP_EVENT", "Analytics disparado: ${event.eventName}")
                        val bundle = Bundle().apply {
                            putString("origin", "server_driven_ui")
                        }
                        firebaseAnalytics.logEvent(event.eventName, bundle)
                    }

                    is DsUiEvent.Action -> {
                        android.util.Log.d("APP_EVENT", "Action recebida: ${event.action}")
                        handleNavigation(event.action)
                    }

                    is DsUiEvent.Submit -> {
                        android.util.Log.d("APP_EVENT", "Submit da tela: ${event.screenId}")
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
                "back" -> {
                    finish()
                }
            }
        }
    }
}