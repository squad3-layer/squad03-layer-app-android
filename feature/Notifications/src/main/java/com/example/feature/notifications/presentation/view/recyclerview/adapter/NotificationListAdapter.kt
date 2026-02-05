package com.example.feature.notifications.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.notifications.databinding.NotificationItemBinding
import com.example.feature.notifications.domain.model.Notification

class NotificationListAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    class ViewHolder(val binding: NotificationItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.binding.txtDateTime.text = notification.dateTime
        holder.binding.txtTitle.text = notification.title
        holder.binding.txtStatus.text = if (notification.isRead) "Lido" else "Não lido"
    }

    override fun getItemCount() = notifications.size
}
