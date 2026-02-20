package com.example.feature.notifications.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.services.analytics.AnalyticsTags
import com.example.services.database.local.entity.NotificationEntity
import com.example.feature.notifications.domain.usecase.DeleteNotificationUseCase
import com.example.feature.notifications.domain.usecase.GetAllNotificationsUseCase
import com.example.feature.notifications.domain.usecase.GetNewNotificationsCountUseCase
import com.example.feature.notifications.domain.usecase.UpdateExpiredNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase,
    private val getNewNotificationsCountUseCase: GetNewNotificationsCountUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase,
    private val updateExpiredNotificationsUseCase: UpdateExpiredNotificationsUseCase,
    val analyticsHelper: AnalyticsTags
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    private val _newNotificationsCount = MutableStateFlow(0)
    val newNotificationsCount: StateFlow<Int> = _newNotificationsCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotifications()
        loadNewNotificationsCount()
        updateExpiredNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            getAllNotificationsUseCase().collect { notificationsList ->
                _notifications.value = notificationsList
                _isLoading.value = false
            }
        }
    }

    private fun loadNewNotificationsCount() {
        viewModelScope.launch {
            getNewNotificationsCountUseCase().collect { count ->
                _newNotificationsCount.value = count
            }
        }
    }

    private fun updateExpiredNotifications() {
        viewModelScope.launch {
            updateExpiredNotificationsUseCase()
        }
    }

    fun deleteNotification(notificationId: Long) {
        viewModelScope.launch {
            deleteNotificationUseCase(notificationId)
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            updateExpiredNotificationsUseCase()
        }
    }
}
