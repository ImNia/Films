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
        selectGenre = if (genre == selectGenre) {
            null
        } else {
            genre
        }
        val filterFilm = filmsFilterByGenre(model.requestData)
        val genres = defineGenres(model.requestData)
        filmView?.showGenresAndFilms(setDataByFilmsAndGenres(genres, filterFilm))
    }

    private fun settingData() {
        val receivedData = model.requestData
        val genres = defineGenres(receivedData)
        filmView?.showGenresAndFilms(setDataByFilmsAndGenres(genres, receivedData))
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