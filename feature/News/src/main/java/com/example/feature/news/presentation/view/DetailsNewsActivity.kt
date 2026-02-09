package com.example.feature.news.presentation.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityDetailsNewsBinding
import com.example.feature.news.presentation.viewModel.DetailNewsViewModel
import com.example.mylibrary.ds.text.DsText
import kotlinx.coroutines.launch
import kotlin.getValue

class DetailsNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsNewsBinding

    private val viewModel: DetailNewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val newsId = intent.getStringExtra("NEWS_ID") ?: ""
        setupToolbar()
        setupFavoriteListener(newsId)
    }
    private fun setupToolbar() {
        binding.toolbar.apply {
            setBackButton(show = true) {
                finish()
            }
        }
    }
    private fun setupFavoriteListener(newsId: String) {
        binding.icFavoriteButton?.setOnClickListener {
            viewModel.toggleFavorite(newsId)
        }

        lifecycleScope.launch {
            viewModel.isFavorite.collect { fav ->
                binding.icFavoriteButton?.isSelected = fav

                val iconRes = if (fav) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                binding.icFavoriteButton?.setImageResource(iconRes)

                if (fav) animateHeart()
            }
        }
    }

    private fun animateHeart() {
        binding.icFavoriteButton?.animate()
            ?.scaleX(1.2f)?.scaleY(1.2f)
            ?.setDuration(100)
            ?.withEndAction {
                binding.icFavoriteButton?.animate()?.scaleX(1.0f)?.scaleY(1.0f)
            }
    }
}