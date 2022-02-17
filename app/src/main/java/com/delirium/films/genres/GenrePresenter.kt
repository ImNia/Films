package com.delirium.films.genres

import com.delirium.films.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException

class GenrePresenter(view: GenreView, private val filmRequest: FilmsRequest) {
    private var genreView: GenreView = view

    var resultData: List<FilmInfo> = listOf()

    fun getAllMovieList() {
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                resultData = response.body()?.films as List<FilmInfo>
                drawGenresAndFilms(resultData)
            }
        } )
    }

    private fun drawGenresAndFilms(filmsInfo: List<FilmInfo>) {
        val genres: MutableList<String> = mutableListOf()
        for(itemFilmsItem in filmsInfo) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }

        genreView.drawGenresAndFilms(dataSetFill(genres, filmsInfo))
    }

    fun drawFilms(genre: String?) {
        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        if (genre == null) {
            resultData.forEach {
                filmListFilter.add(it)
            }
        } else {
            resultData.forEach {
                if (it.genres.contains(genre)) {
                    filmListFilter.add(it)
                }
            }
        }

        genreView.updateFilm(dataSetFill(listOf(), filmListFilter))
    }

    fun drawDataAfterRotate(genre: String) : MutableList<ModelAdapter>{
        val genres: MutableList<String> = mutableListOf()
        for(itemFilmsItem in resultData) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }

        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        resultData.forEach {
            if (it.genres.contains(genre)) {
                filmListFilter.add(it)
            }
        }

        return dataSetFill(genres, filmListFilter)
    }

    fun getFilmInfo(id: Int) : FilmInfo {
        var currentFilm: FilmInfo? = null
        for(item in resultData) {
            if (item.id == id)
                currentFilm = item
        }
        return currentFilm ?: throw IllegalArgumentException()
    }

    private fun dataSetFill(
        genres: List<String>,
        filmsInfo: List<FilmInfo>
    ) : MutableList<ModelAdapter> {
        val dataSet = mutableListOf<ModelAdapter>()

        if(genres.isNotEmpty()) {
            dataSet.add(Titles(
                Title(
                    title = "GENRES",
                    rusTitle = "Жанры"
                )
            ))
            genres.forEach{
                dataSet.add(Genres(genre = it))
            }

            dataSet.add(Titles(
                Title(
                    title = "FILM",
                    rusTitle = "Фильмы"
                )
            ))
        }
        filmsInfo.forEach{
            dataSet.add(Films(film = it))
        }

        return dataSet
    }

    companion object {
        fun newInstance(view: GenreView) : GenrePresenter {
            return GenrePresenter(view, Common.filmsRequest)
        }
    }
}