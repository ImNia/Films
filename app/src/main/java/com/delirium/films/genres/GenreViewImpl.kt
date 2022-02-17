package com.delirium.films.genres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.ClickElement
import com.delirium.films.FilmAdapter
import com.delirium.films.R
import com.delirium.films.databinding.FragmentGenreBinding
import com.delirium.films.model.*

class GenreViewImpl : Fragment(), GenreView {
    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridManager: GridLayoutManager

    private var selectValue: String? = null

    private val genrePresenter: GenrePresenter = GenrePresenter.newInstance(this)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SELECT_VALUE", selectValue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState != null && savedInstanceState.containsKey("SELECT_VALUE")) {
            selectValue = savedInstanceState.getString("SELECT_VALUE")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingGenre = DataBindingUtil.inflate<FragmentGenreBinding>(inflater,
            R.layout.fragment_genre, container, false)

        adapter = FilmAdapter(ClickElement { element, type ->
            if (type == "film") {
                val film = genrePresenter.getFilmInfo(element.toInt())
                val bundle = bundleOf("film" to film)
                bundle.putString("titleFilm", film.localized_name)
                bindingGenre.root.findNavController().navigate(
                    R.id.action_genreViewImpl_to_descriptionFilm, bundle
                )
            } else if (type == "genre") {
                selectValue = if (element == selectValue) {
                    null
                } else {
                    element
                }
                adapter.selectValue = selectValue
                genrePresenter.drawFilms(selectValue)
            }
        })

        gridManager = GridLayoutManager(activity, 2)
        recyclerView = bindingGenre.recycler
        genrePresenter.getAllMovieList()

        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    FilmAdapter.GENRE_TYPE -> 2
                    FilmAdapter.FILM_INFO_TYPE -> 1
                    FilmAdapter.TITLE_TYPE -> 2
                    else -> throw IllegalArgumentException()
                }
            }
        }

        recyclerView.layoutManager = gridManager

        return bindingGenre.root
    }

    override fun drawGenresAndFilms(dataSet: MutableList<ModelAdapter>) {
        if (selectValue != null) {
            adapter.selectValue = selectValue
            adapter.prevSelectValue = null
            val data = genrePresenter.drawDataAfterRotate(selectValue!!)
            adapter.data = data
            adapter.updateGenre()
        } else {
            adapter.data = dataSet
        }
        recyclerView.adapter = adapter
    }

    override fun updateFilm(dataSet: MutableList<ModelAdapter>) {
        adapter.updateData(dataSet)
    }
}