package com.delirium.films.genres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.*
import com.delirium.films.databinding.FilmsItemBinding
import com.delirium.films.databinding.FragmentGenreBinding
import com.delirium.films.databinding.GenreItemBinding
import com.delirium.films.model.*

class GenreViewImpl : Fragment(), GenreView, ClickElement {
    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridManager: GridLayoutManager

    private lateinit var viewBinding: FragmentGenreBinding
    private lateinit var viewBindingFilm: FilmsItemBinding
    private lateinit var viewBindingGenre: GenreItemBinding

    private val genrePresenter: GenrePresenter by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentGenreBinding.inflate(inflater, container, false)
        gridManager = GridLayoutManager(activity, 2)
        recyclerView = viewBinding.recycler
        recyclerView.layoutManager = gridManager

        viewBindingFilm = FilmsItemBinding.inflate(inflater, container, false)
        viewBindingGenre = GenreItemBinding.inflate(inflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (genrePresenter.genreView == null) {
            genrePresenter.attachView()
        }
        genrePresenter.getViewFragment(this)

        adapter = FilmAdapter(this)
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
    }

    override fun drawGenresAndFilms(dataSet: MutableList<ModelAdapter>) {
        if (genrePresenter.selectGenre != null) {
            adapter.selectValue = genrePresenter.selectGenre
            adapter.prevSelectValue = null
            val data = genrePresenter.drawDataAfterRotate(genrePresenter.selectGenre!!)
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

    override fun onClickFilm(name: String) {
        val film = genrePresenter.getFilmInfo(name)
        val bundle = bundleOf("film" to film)
        bundle.putString("titleFilm", film.localized_name)
        viewBinding.root.findNavController().navigate(
            R.id.action_genreViewImpl_to_descriptionFilm, bundle
        )
    }

    override fun onClickGenre(genre: String) {
        genrePresenter.changeSelectGenre(genre)
        adapter.selectValue = genrePresenter.selectGenre
        genrePresenter.drawFilms(genrePresenter.selectGenre)
    }
}