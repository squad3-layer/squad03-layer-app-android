package com.example.feature.news.presentation.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.feature.analytics.AnalyticsHelper
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityDetailsNewsBinding
import com.example.feature.news.domain.model.Article
import com.example.feature.news.presentation.viewModel.DetailNewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.example.feature.news.domain.model.News

@AndroidEntryPoint
class DetailsNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsNewsBinding
    private val viewModel: DetailNewsViewModel by viewModels()

    private lateinit var analyticsHelper: AnalyticsHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analyticsHelper = AnalyticsHelper(this)

        val article = intent.getParcelableExtra<Article>("ARTICLE_DATA")



        Log.d("DEBUG_DETAILS", "Artigo recebido: ${article?.title}")


        article?.let { data ->
            analyticsHelper.logViewDetails(data.url ?: data.title)
            setupUI(data)
            setupToolbar()
            setupFavoriteListener(data)

            viewModel.checkIfIsFavorite(data.url)
            binding.icFavoriteButton?.setOnClickListener {
                article?.let {
                    val identifier =it.url ?: it.title
                    if (viewModel.isFavorite.value == true) {
                        analyticsHelper.logRemoveFavorite(identifier)
                    } else {
                        analyticsHelper.logAddToFavorites(identifier) }
                    viewModel.toggleFavorite(it)
                }
            }


            binding.icShareButton?.setOnClickListener {
                article?.let {
                    val identifier = it.url ?: it.title
                    analyticsHelper.logShareNews(identifier)
                }

                shareNews(
                    title = data.title,
                    description = data.description ?: "",
                    url = data.url
                )
            }
        } ?: run {
            Log.e("DEBUG_DETAILS", "Erro: O objeto Article veio nulo da Home!")
        }

        setupWindowInsets()
    }

    private fun setupUI(article: Article) {
        binding.apply {
            detailsNewsTitle.text = article.title
            detailsNewsDescription.text = article.description ?: article.content
            detailsNewsDate.text = article.publishedAt
            detailsNewsAuthor.text = article.author ?: "Autor desconhecido"

            imageView?.load(article.urlToImage) {
                crossfade(true)
                placeholder(com.example.mylibrary.R.drawable.ds_icon_person)
                error(com.example.mylibrary.R.drawable.ds_icon_person)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setBackButton(show = true) {
                finish()
            }
        }
    }

    private fun setupFavoriteListener(article: Article) {
        binding.icFavoriteButton?.setOnClickListener {
            viewModel.toggleFavorite(article)
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

    private fun shareNews(title: String, description: String, url: String) {
        val imageUri = getImageUriFromView()
        val shareText = "📰 *${title}*\n\n${description.take(100)}...\n\nLeia mais em: $url"

        val intent = Intent(Intent.ACTION_SEND).apply {
            if (imageUri != null) {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
            }
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Compartilhar Notícia"))
    }

    private fun getImageUriFromView(): Uri? {
        val drawable = binding.imageView?.drawable as? android.graphics.drawable.BitmapDrawable ?: return null
        val bitmap = drawable.bitmap
        val imagesFolder = File(cacheDir, "images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "shared_image.png")
        val stream = FileOutputStream(file)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}