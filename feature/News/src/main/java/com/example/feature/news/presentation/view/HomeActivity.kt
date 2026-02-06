package com.example.feature.news.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityHomeBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupLogoutButton()
        requestNotificationPermission()
        setupWindowInsets()
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
}
