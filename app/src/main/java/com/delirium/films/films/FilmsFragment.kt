package com.delirium.films.films

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.delirium.films.*
import com.delirium.films.databinding.FragmentFilmsBinding
import com.delirium.films.model.*
import com.google.android.material.snackbar.Snackbar

class FilmsFragment : Fragment(), PageView, ClickElement {
    private lateinit var adapter: FilmAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var gridManager: GridLayoutManager

    private lateinit var filmsBinding: FragmentFilmsBinding

    private var snackBar: Snackbar? = null

    private val presenter: Presenter by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        filmsBinding = FragmentFilmsBinding.inflate(inflater, container, false)
        gridManager = GridLayoutManager(activity, 2)
        recyclerView = filmsBinding.recycler
        recyclerView.layoutManager = gridManager

        return filmsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)

        adapter = FilmAdapter(this)
        presenter.prepareSetting()

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

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }

    override fun showGenresAndFilms(
        additionalInfo: MutableList<ModelAdapter>,
        filmsInfo: MutableList<ModelAdapter>,
        isUpdate: Boolean
    ) {
        if(isUpdate) {
            adapter.updateData(additionalInfo, filmsInfo)
        } else {
            adapter.data = (additionalInfo + filmsInfo) as MutableList<ModelAdapter>
            recyclerView.adapter = adapter
        }
    }

    override fun showFilmDescription(film: FilmInfo) {
        filmsBinding.root.findNavController().navigate(
            //TODO изменить установку заголовка
            FilmsFragmentDirections.actionFilmsFragmentToFilmDescription(film, film.name)
        )
    }

    override fun onClickFilm(name: String) {
        presenter.goToDescriptionFilm(name)
    }

    override fun onClickGenre(genre: String) {
        presenter.changeCurrentGenre(genre)
    }

    override fun showProgressBar() {
        filmsBinding.progressBar.visibility = ProgressBar.VISIBLE
    }

    override fun hideProgressBar() {
        filmsBinding.progressBar.visibility = ProgressBar.INVISIBLE
    }

    override fun snackBarWithError(statusCode: Int?) {
        val textError = when (statusCode) {
            Model.NOT_FOUND -> R.string.server_error
            Model.REQUEST_TIMEOUT -> R.string.request_timeout_error
            else -> R.string.data_not_load
        }

        snackBar = Snackbar
            .make(filmsBinding.recycler, getString(textError), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry_on_error) {
                presenter.retryLoadDataOnError()
            }
        snackBar?.show()
    }

    override fun hideSnackBar() {
        snackBar?.let {
            if (it.isShown) it.dismiss()
        }
    }
}