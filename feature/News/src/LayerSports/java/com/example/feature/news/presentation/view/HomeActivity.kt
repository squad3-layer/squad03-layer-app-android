package com.example.feature.news.presentation.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.domleondev.designsystem.contract.DesignSystem
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityHomeBinding
import com.example.feature.news.presentation.view.recyclerview.adapter.HomeAdapter
import com.example.feature.news.presentation.view.recyclerview.decoration.ItemSpacingDecoration
import com.example.feature.news.presentation.viewModel.HomeViewModel
import com.example.mylibrary.ds.text.DsText
import com.example.navigation.Navigator
import com.example.navigation.routes.NavigationRoute
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var uiRenderer: UiRenderer
    @Inject lateinit var designSystem: DesignSystem

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.fetchRemoteHomeScreen()
        setupWindowInsets()
        requestNotificationPermission()

        observeViewModel()
        observeDesignSystemEvents()

        binding.containerMenu.setOnClickListener {
            binding.containerMenu.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.containerBody.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.containerBody.visibility = View.VISIBLE

                    val screen = state.screen
                    val components = screen.components

                    components.find { it.type == "Header" }?.let {
                        uiRenderer.render(binding.containerHeader,
                            ScreenDefinition(screenName = "header", components = listOf(it)))
                    }

                    val menuComponents = components.filter { it.type == "MenuItem" }
                    if (menuComponents.isNotEmpty()) {
                        uiRenderer.render(binding.menuContent,
                            ScreenDefinition(screenName = "side_menu", components = menuComponents))
                    }

                    val bodyComponents = components.filter {
                        it.type != "Header" && it.type != "MenuItem"
                    }

                    if (bodyComponents.isNotEmpty()) {
                        uiRenderer.render(
                            binding.containerBody,
                            ScreenDefinition(screenName = "home_body", components = bodyComponents)
                        )
                    }
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    android.widget.Toast.makeText(this, state.message, android.widget.Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
    }

    private fun observeDesignSystemEvents() {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                when (event) {
                    is DsUiEvent.Action -> {
                        when (event.action) {
                            "menu:open" -> binding.containerMenu.visibility = View.VISIBLE
                            "auth:logout" -> performLogout()
                            "navigate:back" -> finish()
                            "navigation:filters" -> {
                                val intent = Intent(this@HomeActivity,FiltersActivity::class.java)
                                startActivity(intent)
                            }
                            "navigate:favorites" -> {
                                val intent = Intent(this@HomeActivity,FavoriteActivity::class.java)
                                startActivity(intent)
                            }
                            "navigate:notifications" -> {
                                handleNotificationsClick()
                            }
                            "navigate:details" -> {

                                val selectedArticle = (viewModel.uiState.value as? UiState.Success)?.screen?.components
                                    ?.find { it.id == event.componentId }
                                if (selectedArticle != null) {
                                    val intent = Intent(this@HomeActivity, DetailsNewsActivity::class.java)

                                    val articleData = viewModel.articlesList.find { it.url == event.componentId }

                                    intent.putExtra("ARTICLE_DATA", articleData)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun performLogout() {
        auth.signOut()
        navigator.navigateToActivity(this, NavigationRoute.Login(false))
        finish()
    }

    private fun handleNotificationsClick(): Boolean {
        navigator.navigateToActivity(this, NavigationRoute.Notifications)
        return true
    }
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}



