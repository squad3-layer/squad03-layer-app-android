package com.example.feature.notifications.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.notifications.databinding.ActivityNotificationsBinding
import com.example.feature.notifications.presentation.view.recyclerview.adapter.NotificationListAdapter
import com.example.feature.notifications.presentation.viewModel.NotificationsViewModel

class NotificationsActivity : ComponentActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)

        viewModel.notifications.observe(this) { notifications ->
            binding.recyclerNotifications.adapter = NotificationListAdapter(notifications)
        }

        viewModel.loadNotifications()
        viewModel.observeNotificationsRealtime()
    }
}
