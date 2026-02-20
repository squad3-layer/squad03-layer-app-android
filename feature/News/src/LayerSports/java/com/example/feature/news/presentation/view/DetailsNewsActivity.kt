package com.example.feature.news.presentation.view



import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.domleondev.designsystem.contract.DesignSystem
import com.domleondev.designsystem.contract.DsUiEvent
import com.domleondev.designsystem.domain.model.ScreenDefinition
import com.domleondev.designsystem.domain.renderer.UiRenderer
import com.domleondev.designsystem.presentation.state.UiState
import com.example.feature.news.databinding.ActivityDetailsNewsBinding
import com.example.feature.news.domain.model.Article
import com.example.feature.news.presentation.viewModel.DetailNewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class DetailsNewsActivity : AppCompatActivity() {

    @Inject
    lateinit var uiRenderer: UiRenderer
    @Inject lateinit var designSystem: DesignSystem
    private val viewModel: DetailNewsViewModel by viewModels()
    private lateinit var binding: ActivityDetailsNewsBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<Article>("ARTICLE_DATA")
        article?.let { viewModel.buildScreen(it) }

        observeViewModel()
        observeDesignSystemEvents(article)

        // Trata o botão back do sistema
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        })
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            if (state is UiState.Success) {

                uiRenderer.render(binding.containerHeader,
                    ScreenDefinition("h", state.screen.components.filter { it.type == "Header" })
                )
                uiRenderer.render(binding.containerBody, ScreenDefinition("b", state.screen.components.filter { it.type != "Header" }))
            }
        }
    }

    private fun observeDesignSystemEvents(article: Article?) {
        lifecycleScope.launch {
            designSystem.eventStream().events.collect { event ->
                if (event is DsUiEvent.Action) {
                    when (event.action) {
                        "navigate:back" -> navigateBack()
                        "news:share" -> article?.let { shareNews(it.title, it.description ?: "", it.url) }
                        "news:favorite" -> article?.let { viewModel.toggleFavorite(it) }
                    }
                }
            }
        }
    }

    private fun navigateBack() {
        if (!isTaskRoot) {
            finish()
        } else {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
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
        val imageView = binding.containerBody.findViewWithTag<android.widget.ImageView>("news_image")
            ?: return null

        val drawable = imageView.drawable as? android.graphics.drawable.BitmapDrawable ?: return null

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
}