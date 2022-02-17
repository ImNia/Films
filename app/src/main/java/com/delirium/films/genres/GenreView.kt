package com.delirium.films.genres

import com.delirium.films.model.ModelAdapter

interface GenreView {
    fun drawGenresAndFilms(dataSet: MutableList<ModelAdapter>)
    fun updateFilm(dataSet: MutableList<ModelAdapter>)
}