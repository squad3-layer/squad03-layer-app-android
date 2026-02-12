package com.example.feature.news.presentation.view.favorites

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityFavoritesBinding
import com.example.feature.news.presentation.view.DetailsNewsActivity
import com.example.feature.news.presentation.view.recyclerview.adapter.FavoritesAdapter
import com.example.feature.news.presentation.view.recyclerview.decoration.ItemSpacingDecoration
import com.example.feature.news.presentation.viewModel.FavoritesViewModel
import com.example.mylibrary.ds.text.DsText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    companion object {
        private const val EXTRA_ARTICLE_DATA = "ARTICLE_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupWindowInsets()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega favoritos toda vez que volta para esta tela
        viewModel.loadFavorites()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setToolbarTitle(context.getString(R.string.favorites_title), DsText.TextStyle.HEADER)
            setBackButton(show = true) {
                finish()
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter { article ->
            val intent = Intent(this, DetailsNewsActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE_DATA, article)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_16)
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(spacingInPixels))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favorites.collect { favorites ->
                adapter.submitList(favorites)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.shimmerLayout.root.isVisible = isLoading

                // Só mostra RecyclerView se não estiver carregando e tiver itens
                val hasFavorites = viewModel.favorites.value.isNotEmpty()
                binding.recyclerView.isVisible = !isLoading && hasFavorites

                // Mostra empty state se não estiver carregando e não tiver itens
                binding.textEmptyState.isVisible = !isLoading && !hasFavorites
            }
        }
    }
}