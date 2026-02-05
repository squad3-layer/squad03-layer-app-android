package com.example.feature.notifications.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feature.notifications.domain.model.Notification
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    // Simula carregamento de notificações
    fun loadNotifications() {
        viewModelScope.launch {
            val data = listOf(
                Notification(id = "1", dateTime = "05/02/2026 14:00", title = "Nova mensagem", isRead = false),
                Notification(id = "2", dateTime = "05/02/2026 13:30", title = "Atualização disponível", isRead = true),
                Notification(id = "3", dateTime = "04/02/2026 18:45", title = "Promoção exclusiva", isRead = false)
            )
            _notifications.value = data
        }
    }


    fun observeNotificationsRealtime() {
        viewModelScope.launch {
            // Aqui você pode simular uma atualização
            val updated = _notifications.value?.map { it.copy(isRead = true) } ?: emptyList()
            _notifications.postValue(updated)
        }
    }
}
