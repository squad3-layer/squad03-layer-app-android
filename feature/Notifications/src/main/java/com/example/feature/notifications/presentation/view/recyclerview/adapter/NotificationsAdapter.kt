package com.example.feature.notifications.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.notifications.databinding.NotificationItemBinding
import com.example.feature.notifications.domain.model.Notification
import com.example.mylibrary.ds.card.notification.DsNotificationCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsAdapter(
    //private val onNotificationClick: (Notification) -> Unit,
    //private val onDeleteClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: NotificationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val card: DsNotificationCard get() = binding.notificationCard

        fun bind(notification: Notification) {
            card.apply {
                card.setTitle(notification.title)
                card.setDateTime(formatDate(notification.sentTimestamp))
                card.setIsNew(notification.isNew)

            }
        }

        private fun formatDate(timestamp: Long): String {
            val localePtBr = Locale("pt", "BR")
            val date = Date(timestamp)
            val sdf = SimpleDateFormat("dd MMM, HH:mm", localePtBr)
            return sdf.format(date).replaceFirstChar { it.titlecase(localePtBr) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(
            oldItem: Notification,
            newItem: Notification
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Notification,
            newItem: Notification
        ) = oldItem == newItem
    }
}
