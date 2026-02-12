package com.example.feature.news.presentation.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.news.R
import com.example.feature.news.databinding.ActivityHomeBinding
import com.example.feature.news.presentation.view.recyclerview.adapter.HomeAdapter
import com.example.feature.news.presentation.view.recyclerview.adapter.NewsLoadStateAdapter
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

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var navigator: Navigator

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeAdapter

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
        private const val POPUP_FIELD_NAME = "mPopup"
        private const val FORCE_SHOW_ICON_METHOD = "setForceShowIcon"
        private const val EXTRA_ARTICLE_DATA = "ARTICLE_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        requestNotificationPermission()
        setupWindowInsets()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setToolbarTitle(context.getString(R.string.news), DsText.TextStyle.HEADER)
            setHamburgerMenu { showHamburgerMenu() }
        }
    }

    private fun showHamburgerMenu() {
        showPopupMenu(binding.toolbar)
    }

    private fun showPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)

        enableMenuIcons(popupMenu)
        setupMenuItemClickListener(popupMenu)

        popupMenu.show()
    }

    private fun enableMenuIcons(popupMenu: PopupMenu) {
        try {
            val popupField = PopupMenu::class.java.getDeclaredField(POPUP_FIELD_NAME)
            popupField.isAccessible = true
            val menuPopup = popupField.get(popupMenu)
            menuPopup?.javaClass
                ?.getDeclaredMethod(FORCE_SHOW_ICON_METHOD, Boolean::class.java)
                ?.invoke(menuPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupMenuItemClickListener(popupMenu: PopupMenu) {
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_favorites -> handleFavoritesClick()
                R.id.menu_notifications -> handleNotificationsClick()
                R.id.menu_logout -> handleLogoutClick()
                else -> false
            }
        }
    }

    private fun handleFavoritesClick(): Boolean {
        navigator.navigateToActivity(this, NavigationRoute.Favorites)
        return true
    }

    private fun handleNotificationsClick(): Boolean {
        navigator.navigateToActivity(this, NavigationRoute.Notifications)
        return true
    }

    private fun handleLogoutClick(): Boolean {
        performLogout()
        return true
    }

    private fun performLogout() {
        auth.signOut()
        navigator.navigateToActivity(
            this,
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
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter { article ->
            val intent = Intent(this, DetailsNewsActivity::class.java).apply {
                putExtra(EXTRA_ARTICLE_DATA, article)
            }
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = NewsLoadStateAdapter { adapter.retry() }
        )

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_16)
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(spacingInPixels))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articles.collect { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadStates ->
                val isLoading = loadStates.refresh is LoadState.Loading

                binding.shimmerLayout.root.isVisible = isLoading
                binding.recyclerView.isVisible = !isLoading
            }
        }
    }
}



