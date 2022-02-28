package com.delirium.films.genres

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.ModelAdapter

interface PageView {
    fun showGenresAndFilms(
        additionalInfo: MutableList<ModelAdapter>,
        filmsInfo: MutableList<ModelAdapter>,
        selectGenre: String?)
    fun showFilmDescription(film: FilmInfo)
    fun showProgressBar()
    fun hideProgressBar()
    fun snackBarWithError()
    fun hideSnackBar()
}