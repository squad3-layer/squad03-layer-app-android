package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritesAdapter(
    private val onItemClick: (Article) -> Unit
) : ListAdapter<Article, FavoritesAdapter.FavoritesViewHolder>(ArticleDiffCallback()) {

    inner class FavoritesViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.root.setOnClickListener {
                onItemClick(article)
            }
            binding.newCard.apply {
                setNews(
                    title = article.title,
                    description = article.description ?: "",
                    time = formatDate(article.publishedAt),
                    imageLoader = {
                        contentDescription = article.title
                        load(article.urlToImage) {
                            crossfade(true)
                            placeholder(com.example.mylibrary.R.drawable.ds_icon_person)
                            error(com.example.mylibrary.R.drawable.ds_icon_person)
                        }
                    }
                )
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
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