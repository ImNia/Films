package com.delirium.films.genres

import androidx.lifecycle.ViewModel
import com.delirium.films.model.*
import java.lang.IllegalArgumentException

class Presenter : ViewModel() {
    var pageView: PageView? = null

    private val model = Model(this)

    var selectGenre: String? = null
        set(currentGenre) {
            field = if (currentGenre == selectGenre) {
                null
            } else {
                currentGenre
            }
        }

    var loadingInProgress: Boolean = false
    var dataReceived: Boolean = false
    var gotError: Boolean = false

    fun setViewFragment(pageViewAttach: PageView) {
        pageView = pageViewAttach
    }

    fun attachView() {
        loadingInProgress = true
        model.getData()
        changeDataForView()
    }

    fun detachView() {
        super.onCleared()
    }

    fun responseOnFailure() {
        gotError = true
        changeDataForView()
    }

    fun changeDataForView() {
        if (model.requestData.isNotEmpty()) {
            pageView?.hideProgressBar()
            dataReceived = true
            loadingInProgress = false
            gotError = false
        }

        if (loadingInProgress && !dataReceived && !gotError) {
            pageView?.showProgressBar()
        } else if (!loadingInProgress && dataReceived && !gotError) {
            pageView?.hideSnackBar()
            defineGenre(model.requestData)
        } else if(gotError) {
            pageView?.snackBarWithError()
        }
    }

    private fun defineGenre(filmsInfo: List<FilmInfo>) {
        val genres: MutableList<String> = mutableListOf()
        for (itemFilmsItem in filmsInfo) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }

        pageView?.drawGenresAndFilms(dataSetFill(genres, filmsInfo))
    }

    fun drawFilterFilms() {
        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        if (selectGenre == null) {
            model.requestData.forEach {
                filmListFilter.add(it)
            }
        } else {
            model.requestData.forEach {
                if (it.genres.contains(selectGenre!!)) {
                    filmListFilter.add(it)
                }
            }
        }

        pageView?.updateFilm(dataSetFill(listOf(), filmListFilter))
    }

    fun drawDataAfterRotate(): MutableList<ModelAdapter> {
        val genres: MutableList<String> = mutableListOf()
        for (itemFilmsItem in model.requestData) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }

        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        model.requestData.forEach {
            if (it.genres.contains(selectGenre)) {
                filmListFilter.add(it)
            }
        }

        return dataSetFill(genres, filmListFilter)
    }

    fun getFilmInfo(name: String): FilmInfo {
        var currentFilm: FilmInfo? = null
        for (item in model.requestData) {
            if (item.localized_name == name)
                currentFilm = item
        }
        return currentFilm ?: throw IllegalArgumentException()
    }

    private fun dataSetFill(
        genres: List<String>,
        filmsInfo: List<FilmInfo>
    ): MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()

        if (genres.isNotEmpty()) {
            dataSet.add(
                Titles(
                    Title(
                        title = "GENRES",
                        rusTitle = "Жанры"
                    )
                )
            )
            genres.forEach {
                dataSet.add(Genres(genre = it))
            }

            dataSet.add(
                Titles(
                    Title(
                        title = "FILM",
                        rusTitle = "Фильмы"
                    )
                )
            )
        }
        filmsInfo.forEach {
            dataSet.add(Films(film = it))
        }

        return dataSet
    }
}