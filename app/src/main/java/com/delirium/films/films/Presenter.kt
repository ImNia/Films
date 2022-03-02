package com.delirium.films.films

import androidx.lifecycle.ViewModel
import com.delirium.films.model.*

class Presenter : ViewModel() {
    private var filmView: FilmView? = null

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

    fun attachView(filmView: FilmView) {
        this.filmView = filmView
    }

    fun detachView() {
        filmView = null
    }

    fun responseOnFailure(statusCode: Int? = null) {
        gotError = true
        prepareSetting(statusCode)
    }

    fun retryLoadDataOnError() {
        filmView?.showProgressBar()
        model.getData()
    }

    fun prepareSetting(statusCode: Int? = null) {
        if (model.requestData.isNotEmpty()) {
            filmView?.hideProgressBar()
            dataReceived = true
            loadingInProgress = false
            gotError = false
        }

        if (loadingInProgress && !dataReceived && !gotError) {
            filmView?.showProgressBar()
        } else if (!loadingInProgress && dataReceived && !gotError) {
            filmView?.hideSnackBar()
            settingData()
        } else if(gotError) {
            filmView?.hideProgressBar()
            filmView?.snackBarWithError(statusCode)
        }
    }

    fun changeCurrentGenre(genre: String) {
        selectGenre = genre
        val filterFilm = filmsFilterByGenre(model.requestData)
        val genres = defineGenres(model.requestData)
        filmView?.showGenresAndFilms(
            setGenresInfo(genres),
            setDataFilms(filterFilm),
            isUpdate = true
        )
    }

    private fun settingData() {
        val receivedData = model.requestData
        val genres = defineGenres(receivedData)
        val filterFilms = filmsFilterByGenre(receivedData)
        filmView?.showGenresAndFilms(
            setAdditionalInfo(genres),
            setDataFilms(filterFilms)
        )
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
        currentFilm?.let { filmView?.showFilmDescription(currentFilm) }
    }

    private fun setGenresInfo(
        genres: List<String>
    ): MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()

        genres.forEach {
            if (it == selectGenre) {
                dataSet.add(Genres(genre = it, isSelected = true))
            } else {
                dataSet.add(Genres(genre = it))
            }
        }

        return dataSet
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
                if(it == selectGenre) {
                    dataSet.add(Genres(genre = it, isSelected = true))
                } else {
                    dataSet.add(Genres(genre = it))
                }
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