package com.example.feature.notifications.presentation.view.recycleview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputBinding
import androidx.recyclerview.widget.RecyclerView

class NotificationListAdapter(
    private val context: Context
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    class ViewHolder(notificationItemBinding: NotificationItemBinding) :
        RecyclerView.ViewHolder(notificationItemBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ViewHolder(
            NotificationItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}