package com.delirium.films.films

import com.delirium.films.model.FilmInfo
import com.delirium.films.model.ModelAdapter
import com.delirium.films.model.StatusCode

interface FilmView {
    fun showGenresAndFilms(
        dataToShow: MutableList<ModelAdapter>
    )
    fun showFilmDescription(film: FilmInfo)
    fun showProgressBar()
    fun hideProgressBar()
    fun snackBarWithError(statusCode: StatusCode?)
    fun hideSnackBar()
}