package com.delirium.films.films

import androidx.lifecycle.ViewModel
import com.delirium.films.model.*

class Presenter : ViewModel() {
    private var filmView: FilmView? = null
    private var selectGenre: String? = null
    private val model = Model(this)

    var loadingInProgress: Boolean = false
    var dataReceived: Boolean = false
    var gotError: Boolean = false

    init {
        loadingInProgress = true
        model.getData()
        changeStateView(null)
    }

    fun attachView(filmView: FilmView) {
        this.filmView = filmView
    }

    fun detachView() {
        filmView = null
    }

    private fun changeStateView(statusCode: StatusCode?) = when {
        loadingInProgress -> filmView?.showProgressBar()
        dataReceived -> {
            filmView?.hideSnackBar()
            settingData()
        }
        gotError -> {
            filmView?.hideProgressBar()
            filmView?.snackBarWithError(statusCode)
        }
        else -> Unit
    }

    fun responseOnFailure(statusCode: StatusCode? = null) {
        gotError = true
        loadingInProgress = false
        loadData(statusCode)
    }

    fun retryLoadDataOnError() {
        filmView?.showProgressBar()
        gotError = false
        loadingInProgress = true
        model.getData()
    }

    fun loadData(statusCode: StatusCode? = null) {
        if (model.requestData.isNotEmpty()) {
            filmView?.hideProgressBar()
            dataReceived = true
            loadingInProgress = false
            gotError = false
        }

        changeStateView(statusCode)
    }

    fun changeCurrentGenre(genre: String) {
        selectGenre = if (genre == selectGenre) {
            null
        } else {
            genre
        }
        settingData()
    }

    private fun settingData() {
        val receivedData = model.requestData
        val genres = defineGenres(receivedData)
        val filterFilm = filmsFilterByGenre(receivedData)
        filmView?.showGenresAndFilms(setDataByFilmsAndGenres(genres, filterFilm))
    }

    private fun defineGenres(filmsInfo: List<FilmInfo>) =
        filmsInfo.flatMap { it.genres }.distinct().sorted()

    private fun filmsFilterByGenre(filmsInfo: List<FilmInfo>) = if(selectGenre != null) {
        filmsInfo.filter { film: FilmInfo -> film.genres.contains(selectGenre) }
    } else {
        filmsInfo
    }

    fun goToDescriptionFilm(name: String) {
        var currentFilm: FilmInfo? = null
        for (item in model.requestData) {
            if (item.localized_name == name)
                currentFilm = item
        }
        currentFilm?.let { filmView?.showFilmDescription(currentFilm) }
    }

    private fun setDataByFilmsAndGenres(
        genres: List<String>,
        filmsInfo: List<FilmInfo>
    ): MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()

        dataSet.add(Titles("Жанры")) //TODO R.string.genre_title
        genres.forEach { dataSet.add(Genres(genre = it, isSelected = it == selectGenre)) }
        dataSet.add(Titles("Фильмы")) //TODO R.string.film_title

        filmsInfo.forEach { dataSet.add(Films(film = it)) }
        return dataSet
    }
}