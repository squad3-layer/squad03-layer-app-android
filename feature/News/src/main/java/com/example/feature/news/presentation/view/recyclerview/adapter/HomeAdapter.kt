package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.News

class HomeAdapter(
    private var items: List<News>
) : RecyclerView.Adapter<HomeAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.tvTitle.text = news.title
            binding.tvDescription.text = news.description
            binding.tvDate.text = news.date

            // Carregar imagem com Coil
            binding.imgCover.load(news.imageUrl) {
                crossfade(true)
                placeholder(com.example.mylibrary.R.drawable.ds_icon_person) //arrumar
                error(com.example.mylibrary.R.drawable.ds_icon_person) // arrumar
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<News>) {
        items = newItems
        notifyDataSetChanged()
    }
}
