package com.delirium.films.films

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.ModelAdapter

interface PageView {
    fun showGenresAndFilms(
        additionalInfo: MutableList<ModelAdapter>,
        filmsInfo: MutableList<ModelAdapter>,
        isUpdate: Boolean = false
    )
    fun showFilmDescription(film: FilmInfo)
    fun showProgressBar()
    fun hideProgressBar()
    fun snackBarWithError(statusCode: Int?)
    fun hideSnackBar()
}