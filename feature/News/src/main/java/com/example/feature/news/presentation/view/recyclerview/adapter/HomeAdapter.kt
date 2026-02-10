package com.example.feature.news.presentation.view.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.feature.news.databinding.NewsItemBinding
import com.example.feature.news.domain.model.Article
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeAdapter(
    private var items: List<Article>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: Article) {
           binding.newCard.apply {
               setNews(
                   title = news.title,
                   description = news.description ?: "",
                   time = formatDate(news.publishedAt),
                   imageLoader = {
                       contentDescription = news.title
                       load(news.urlToImage) {
                           crossfade(true)
                           placeholder(com.example.mylibrary.R.drawable.ds_icon_person) //arrumar
                           error(com.example.mylibrary.R.drawable.ds_icon_person) // arrumar
                       }
                   }
               )
           }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val localePtBr = Locale.forLanguageTag("pt-BR")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                val date = inputFormat.parse(dateString) ?: return dateString

                val calendar = Calendar.getInstance()
                calendar.time = date

                val today = Calendar.getInstance()
                val yesterday = Calendar.getInstance()
                yesterday.add(Calendar.DAY_OF_YEAR, -1)

                val timeFormat = SimpleDateFormat("HH:mm", localePtBr)
                val dateFormat = SimpleDateFormat("dd/MM", localePtBr)

                when {
                    isSameDay(calendar, today) -> "Hoje, ${timeFormat.format(date)}"
                    isSameDay(calendar, yesterday) -> "Ontem, ${timeFormat.format(date)}"
                    else -> dateFormat.format(date)
                }
            } catch (e: Exception) {
                dateString
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
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
