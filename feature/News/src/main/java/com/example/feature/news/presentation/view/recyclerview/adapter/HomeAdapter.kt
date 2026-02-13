package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.Article
import com.example.feature.news.utils.formatDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeAdapter(
    private val onItemClick: (Article) -> Unit
) : PagingDataAdapter<Article, HomeAdapter.HomeViewHolder>(ArticleDiffCallback()) {

    inner class HomeViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: Article) {
            binding.root.setOnClickListener {
                onItemClick(news)
            }
            binding.newCard.apply {
                setNews(
                    title = news.title,
                    description = news.description ?: "",
                    time = formatDate(news.publishedAt),
                    imageLoader = {
                        contentDescription = news.title
                        load(news.urlToImage) {
                            crossfade(true)
                            placeholder(com.example.mylibrary.R.drawable.ds_icon_person)
                            error(com.example.mylibrary.R.drawable.ds_icon_person)
                        }
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val article = getItem(position)
        article?.let { holder.bind(it) }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
