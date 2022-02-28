package com.delirium.films.genres

import androidx.lifecycle.ViewModel
import com.delirium.films.model.*

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

    init {
        loadingInProgress = true
        model.getData()
    }

    fun attachView(pageViewAttach: PageView) {
        pageView = pageViewAttach
    }

    fun detachView() {
        pageView = null
    }

    fun responseOnFailure() {
        gotError = true
        prepareSetting()
    }

    fun prepareSetting() {
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
            settingData()
        } else if(gotError) {
            pageView?.hideProgressBar()
            pageView?.snackBarWithError()
        }
    }

    fun changeCurrentGenre(genre: String) {
        selectGenre = genre
        val filterFilm = filmsFilterByGenre(model.requestData)
        pageView?.showGenresAndFilms(
            mutableListOf(),
            setDataFilms(filterFilm),
            selectGenre
        )
    }

    private fun settingData() {
        val receivedData = model.requestData
        val genres = defineGenres(receivedData)
        val filterFilms = filmsFilterByGenre(receivedData)
        pageView?.showGenresAndFilms(
            setAdditionalInfo(genres),
            setDataFilms(filterFilms),
            selectGenre)
    }

    private fun defineGenres(filmsInfo: List<FilmInfo>) : List<String> {
        val genres: MutableList<String> = mutableListOf()
        for (itemFilmsItem in filmsInfo) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }
        return genres
    }

    private fun filmsFilterByGenre(filmsInfo: List<FilmInfo>) : List<FilmInfo> {
        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        if (selectGenre == null) {
            filmsInfo.forEach {
                filmListFilter.add(it)
            }
        } else {
            filmsInfo.forEach {
                if (it.genres.contains(selectGenre!!)) {
                    filmListFilter.add(it)
                }
            }
        }

        return filmListFilter
    }

    fun goToDescriptionFilm(name: String) {
        var currentFilm: FilmInfo? = null
        for (item in model.requestData) {
            if (item.localized_name == name)
                currentFilm = item
        }
        currentFilm?.let { pageView?.showFilmDescription(currentFilm) }
    }

    private fun setAdditionalInfo(
        genres: List<String>
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

        return dataSet
    }

    private fun setDataFilms(
        filmsInfo: List<FilmInfo>
    ): MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()
        filmsInfo.forEach {
            dataSet.add(Films(film = it))
        }
        return dataSet
    }
}