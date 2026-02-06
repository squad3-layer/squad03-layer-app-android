package com.example.feature.notifications.presentation.view

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feature.notifications.R
import com.example.feature.notifications.databinding.ActivityNotificationsBinding
import com.example.feature.notifications.domain.model.toDomain
import com.example.feature.notifications.presentation.view.recyclerview.adapter.NotificationsAdapter
import com.example.feature.notifications.presentation.viewModel.NotificationsViewModel
import com.example.mylibrary.ds.text.DsText
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
            viewModel.notifications.collect { notifications ->
                if (notifications.isNullOrEmpty()) {
                binding.notificationEmpty.visibility = View.VISIBLE
                binding.recyclerNotifications.visibility = View.GONE
                } else {
                    binding.notificationEmpty.visibility = View.GONE
                    binding.recyclerNotifications.visibility = View.VISIBLE
                    adapter.submitList(notifications.map { it.toDomain() })
                }
            }
        }

        lifecycleScope.launch {
            viewModel.newNotificationsCount.collect { count ->
                //TODO: Exibir badge ou atualizar UI com a contagem de novas notificações
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                //TODO: Exibir ou ocultar um indicador de carregamento com base no estado de carregamento
            }
        }

        binding.toolbar.apply {
            setToolbarTitle("Notificações", DsText.TextStyle.HEADER, centered = true)
            setBackButton(show = true) {
                finish()
            }
        }
    }
}
