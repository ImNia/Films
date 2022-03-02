package com.delirium.films.films

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.ModelAdapter

interface FilmView {
    fun showGenresAndFilms(
        //TODO rename
        filmsInfo: MutableList<ModelAdapter>
    )
    fun showFilmDescription(film: FilmInfo)
    fun showProgressBar()
    fun hideProgressBar()
    fun snackBarWithError(statusCode: Int?)
    fun hideSnackBar()
}