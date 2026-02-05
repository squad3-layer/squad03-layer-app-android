package com.example.feature.notifications.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.notifications.databinding.ActivityNotificationsBinding
import com.example.feature.notifications.domain.model.toDomain
import com.example.feature.notifications.presentation.view.recyclerview.adapter.NotificationsAdapter
import com.example.feature.notifications.presentation.viewModel.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    private val adapter by lazy { NotificationsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        // Observar lista de notificações
        lifecycleScope.launch {
            viewModel.notifications.collect { notifications ->
                adapter.submitList(notifications.map { it.toDomain() })
                // Mostrar empty state se necessário
            }
        }
        // Observar contador de novas notificações
        lifecycleScope.launch {
            viewModel.newNotificationsCount.collect { count ->
                //  updateBadge(count)
            }
        }
        // Observar loading state
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
            }
        }
    }
}
