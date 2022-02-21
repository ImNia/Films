package com.delirium.films.genres

import com.delirium.films.model.ModelAdapter

interface PageView {
    fun drawGenresAndFilms(dataSet: MutableList<ModelAdapter>)
    fun updateFilm(dataSet: MutableList<ModelAdapter>)
    fun showProgressBar()
    fun hideProgressBar()
    fun progressBarWithError()
}