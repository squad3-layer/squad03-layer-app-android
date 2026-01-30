package com.example.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.feature.authentication.R
import com.example.feature.authentication.presentation.login.view.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val delayBeforeStart = 500L
    private val animDuration = 900L
    private val extraDelay = 300L

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(com.example.main.R.layout.activity_splash)

        val logo = findViewById<ImageView>(com.example.main.R.id.logo)

        logo.visibility = View.INVISIBLE

        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_anim)

        lifecycleScope.launch {

            delay(delayBeforeStart)

            logo.visibility = View.VISIBLE

            logo.startAnimation(logoAnim)

            delay(animDuration + extraDelay)

            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}