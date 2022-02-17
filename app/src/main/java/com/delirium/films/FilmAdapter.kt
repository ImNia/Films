package com.delirium.films

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.databinding.FilmsItemBinding
import com.delirium.films.databinding.GenreItemBinding
import com.delirium.films.model.*

class FilmAdapter(private val clickElementListener: ClickElement)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = mutableListOf<ModelAdapter>()
    var selectValue: String? = null
    var prevSelectValue: String? = null

    class GenreViewHolder(var binding: GenreItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Genres, clickElementListener: ClickElement) {
            binding.genreVariable = item.genre
            binding.genreListener = clickElementListener
            binding.type = "genre"
        }
    }

    class FilmViewHolder(var binding: FilmsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilmInfo, clickElementListener: ClickElement) {
            binding.filmVariable = item
            binding.filmListener = clickElementListener
            binding.type = "film"
        }
    }

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleName: TextView = itemView.findViewById(R.id.title_name)

        fun bind(item: Titles) {
            titleName.text = item.titleBlock.rusTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
            GENRE_TYPE -> {
                val itemView =
                    GenreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GenreViewHolder(itemView)
            }
            FILM_INFO_TYPE -> {
                val itemView =
                    FilmsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FilmViewHolder(itemView)
            }
            TITLE_TYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
                TitleViewHolder(itemView)
            }
            else -> throw IllegalArgumentException()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is GenreViewHolder && item is Genres) {
            holder.bind(item, clickElementListener)
            if(item.genre == selectValue) {
                holder.binding.genreFilm.background =
                    ContextCompat.getDrawable(holder.binding.genreFilm.context, R.drawable.rounded_corner_selected)
            } else {
                holder.binding.genreFilm.background =
                    ContextCompat.getDrawable(holder.binding.genreFilm.context, R.drawable.rounded_corner)
            }
        } else if (holder is FilmViewHolder && item is Films) {
            holder.bind(item.film, clickElementListener)
        } else if (holder is TitleViewHolder && item is Titles) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int) = when(data[position]) {
            is Genres -> GENRE_TYPE
            is Films -> FILM_INFO_TYPE
            is Titles -> TITLE_TYPE
            else -> throw IllegalArgumentException() //TODO class exception
        }

    override fun getItemCount() : Int {
        return data.size
    }

    fun updateData(dataSet: MutableList<ModelAdapter>) {
        val startIndex  = data.count { it is Genres || it is Titles }
        val endIndex = data.count()
        data.subList(startIndex, endIndex).clear()
        data.addAll(dataSet)
        notifyItemRangeRemoved(startIndex, endIndex - startIndex)
        notifyItemRangeChanged(startIndex, dataSet.size)
        updateGenre()
//        notifyDataSetChanged()
    }

    fun updateGenre() {
        for(item in data) {
            if(item is Genres && item.genre == selectValue) {
                notifyItemChanged(data.indexOf(item))
            } else if (item is Genres && item.genre == prevSelectValue) {
                notifyItemChanged(data.indexOf(item))
            }
        }

        prevSelectValue = selectValue
    }

    companion object {
        const val GENRE_TYPE = 0
        const val FILM_INFO_TYPE = 1
        const val TITLE_TYPE = 2
    }
}

class ClickElement(val clickListener: (element: String, type: String) -> Unit) {
    fun onClick(item: String, type: String) {
        clickListener(item, type)
    }

    fun onClickFilm(item: Int, type: String) {
        clickListener(item.toString(), type)
    }
}