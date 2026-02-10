package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.Article

class HomeAdapter(
    private var items: List<Article>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: Article) {
            binding.tvTitle.text = news.title
            binding.tvDescription.text = news.description
            binding.tvDate.text = news.publishedAt

            // Carregar imagem com Coil
            binding.imgCover.load(news.urlToImage) {
                crossfade(true)
                placeholder(com.example.mylibrary.R.drawable.ds_icon_person) //arrumar
                error(com.example.mylibrary.R.drawable.ds_icon_person) // arrumar
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Article>?) {
        if (newItems != null) {
            items = newItems
            notifyDataSetChanged()
        }
    }
}
