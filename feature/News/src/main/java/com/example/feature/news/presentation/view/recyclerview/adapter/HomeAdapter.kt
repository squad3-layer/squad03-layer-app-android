package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.NewsModel

class HomeAdapter(
    private var items: List<NewsModel>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvDescription.text = item.description
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<NewsModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
