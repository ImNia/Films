package com.delirium.films.genres

import androidx.lifecycle.ViewModel
import com.delirium.films.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.util.*

class Presenter : ViewModel() {
    var pageView: PageView? = null

    private val filmRequest: FilmsRequest = Common.filmsRequest

    var selectGenre: String? = null
    var resultData: List<FilmInfo> = listOf()

    fun getViewFragment(pageViewAttach: PageView) {
        pageView = pageViewAttach
    }

    fun attachView() {
        pageView?.showProgressBar()
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                pageView?.hideProgressBarWithError()
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                resultData = response.body()?.films as List<FilmInfo>
                getAllMovieList()
                pageView?.hideProgressBar()
            }
        })
    }

    fun detachView() {
        super.onCleared()
    }

    fun getAllMovieList() {
        drawGenresAndFilms(resultData)
    }

    private fun drawGenresAndFilms(filmsInfo: List<FilmInfo>) {
        var genres: MutableList<String> = mutableListOf()
        for (itemFilmsItem in filmsInfo) {
            for (itemGenre in itemFilmsItem.genres) {
                if (!genres.contains(itemGenre))
                    genres.add(itemGenre)
            }
        }

        genres = genres.map { genre ->
            genre.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        } as MutableList<String>

        pageView?.drawGenresAndFilms(dataSetFill(genres, filmsInfo))
    }

    fun drawFilms(genre: String?) {
        val filmListFilter: MutableList<FilmInfo> = mutableListOf()

        if (genre == null) {
            resultData.forEach {
                filmListFilter.add(it)
            }
        } else {
            resultData.forEach {
                if (it.genres.contains(genre.lowercase())) {
                    filmListFilter.add(it)
                }
            }
        }

        pageView?.updateFilm(dataSetFill(listOf(), filmListFilter))
    }

    fun drawDataAfterRotate(genre: String): MutableList<ModelAdapter> {
        val genres: MutableList<String> = mutableListOf()
        for (itemFilmsItem in resultData) {
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

    fun getFilmInfo(name: String): FilmInfo {
        var currentFilm: FilmInfo? = null
        for (item in resultData) {
            if (item.localized_name == name)
                currentFilm = item
        }
        return currentFilm ?: throw IllegalArgumentException()
    }

    private fun dataSetFill(
        genres: List<String>,
        filmsInfo: List<FilmInfo>
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
                dataSet.add(Genres(genre = it))
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
        filmsInfo.forEach {
            dataSet.add(Films(film = it))
        }

        return dataSet
    }

    fun changeSelectGenre(currentGenre: String?) {
        selectGenre = if (currentGenre == selectGenre) {
            null
        } else {
            currentGenre
        }
    }
}