package com.delirium.films.genres

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.*
import com.delirium.films.databinding.FilmsItemBinding
import com.delirium.films.databinding.FragmentPageViewBinding
import com.delirium.films.databinding.GenreItemBinding
import com.delirium.films.model.*

class PageViewFragment : Fragment(), PageView, ClickElement {
    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridManager: GridLayoutManager

    private lateinit var pageViewBinding: FragmentPageViewBinding
    private lateinit var filmViewBinding: FilmsItemBinding
    private lateinit var genreViewBinding: GenreItemBinding

    private val presenter: Presenter by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pageViewBinding = FragmentPageViewBinding.inflate(inflater, container, false)
        gridManager = GridLayoutManager(activity, 2)
        recyclerView = pageViewBinding.recycler
        recyclerView.layoutManager = gridManager

        filmViewBinding = FilmsItemBinding.inflate(inflater, container, false)
        genreViewBinding = GenreItemBinding.inflate(inflater, container, false)

        return pageViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (presenter.pageView == null) {
            presenter.attachView()
        }
        presenter.getViewFragment(this)

        adapter = FilmAdapter(this)
        presenter.getAllMovieList()

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
        if (presenter.selectGenre != null) {
            adapter.selectValue = presenter.selectGenre
            adapter.prevSelectValue = null
            val data = presenter.drawDataAfterRotate(presenter.selectGenre!!)
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

    override fun showProgressBar() {
        pageViewBinding.progressBar.visibility = ProgressBar.VISIBLE
    }

    override fun hideProgressBar() {
        pageViewBinding.progressBar.visibility = ProgressBar.INVISIBLE
    }

    override fun hideProgressBarWithError() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.data_not_load)
            .setCancelable(false)
            .setPositiveButton(R.string.retry_on_error) { dialog, _ ->
                dialog.cancel()
                presenter.attachView()
            }
        val alertDialog = builder.create()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.show()
    }

    override fun onClickFilm(name: String) {
        val film = presenter.getFilmInfo(name)
        val bundle = bundleOf("film" to film)
        bundle.putString("titleFilm", film.localized_name)
        pageViewBinding.root.findNavController().navigate(
            R.id.action_genreViewImpl_to_descriptionFilm, bundle
        )
    }

    override fun onClickGenre(genre: String) {
        presenter.changeSelectGenre(genre)
        adapter.selectValue = presenter.selectGenre
        presenter.drawFilms(presenter.selectGenre)
    }
}