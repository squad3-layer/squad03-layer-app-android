package com.example.feature.notifications.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.notifications.R
import com.example.feature.notifications.databinding.ActivityNotificationsBinding
import com.example.feature.notifications.domain.model.toDomain
import com.example.feature.notifications.presentation.view.recyclerview.adapter.NotificationsAdapter
import com.example.feature.notifications.presentation.viewModel.NotificationsViewModel
import com.example.mylibrary.ds.text.DsText
import com.example.navigation.Navigator
import com.example.navigation.routes.NavigationRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    private var hasLoggedEmptyState = false

    private val adapter by lazy {
        NotificationsAdapter(
            onNotificationClick = { notification ->
                viewModel.analyticsHelper.logEvent("notification_item_click", mapOf(
                    "notification_id" to notification.id.toString(),
                    "notification_description" to notification.description
                ))
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.analyticsHelper.logScreenView("notifications_screen_view")

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.screen_view_notifications)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecycler()
        observeViewModel()
    }

    private fun setupRecycler() {
        binding.recyclerNotifications.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = this@NotificationsActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            combine(
                viewModel.notifications,
                viewModel.isLoading
            ) { notifications, isLoading ->
                notifications to isLoading
            }.collect { (notifications, isLoading) ->
                if (!isLoading) {
                    if (notifications.isEmpty()) {
                        binding.notificationEmpty.visibility = View.VISIBLE
                        binding.recyclerNotifications.visibility = View.GONE
                        if (!hasLoggedEmptyState) {
                            viewModel.analyticsHelper.logEvent("notifications_empty_shown", mapOf("is_empty" to "true"))
                            hasLoggedEmptyState = true
                        }
                    } else {
                        binding.notificationEmpty.visibility = View.GONE
                        binding.recyclerNotifications.visibility = View.VISIBLE
                        adapter.submitList(notifications.map { it.toDomain() })
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.newNotificationsCount.collect { count ->
                //TODO: Exibir badge ou atualizar UI com a contagem de novas notificações
            }
        }


        binding.toolbar.apply {
            setToolbarTitle("Notificações", DsText.TextStyle.HEADER, centered = true)
            setBackButton(show = true) {
                navigateBack()
            }
        }
    }

    private fun navigateBack() {
        if (!isTaskRoot) {
            finish()
        } else {
            navigator.navigateToActivity(
                this@NotificationsActivity,
                NavigationRoute.Home
            )
            finish()
        }
    }
}
