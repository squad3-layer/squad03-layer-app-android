package com.example.feature.news.presentation.view

import android.Manifest
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityHomeBinding
import com.example.feature.news.presentation.view.recyclerview.adapter.HomeAdapter
import com.example.feature.news.presentation.viewModel.HomeViewModel
import com.example.mylibrary.ds.text.DsText
import com.example.navigation.Navigator
import com.example.navigation.routes.NavigationRoute
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var navigator: Navigator

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLogoutButton()
        requestNotificationPermission()
        setupWindowInsets()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setToolbarTitle("Home", DsText.TextStyle.DESCRIPTION)
            setBackButton(show = true) {
                finish()
            }
            setActionButtons(
                action1Icon = com.example.mylibrary.R.drawable.ds_icon_notification,
                action1Click = {
                    viewModel.logNotificationClick()
                    navigator.navigateToActivity(
                        this@HomeActivity,
                        NavigationRoute.Notifications
                    )
                }
            )
        }
    }

    private fun setupLogoutButton() {
        binding.buttonClickLogout.setDsClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        auth.signOut()

        navigator.navigateToActivity(
            this@HomeActivity,
            NavigationRoute.Login(redirectToNotifications = false)
        )
        finish()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

    private fun setupRecyclerView() {
        adapter = HomeAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.articles.observe(this) { articles ->
            adapter.updateData(articles)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}



