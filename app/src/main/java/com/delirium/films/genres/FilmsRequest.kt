package com.delirium.films.genres

import com.delirium.films.model.FilmList
import retrofit2.Call
import retrofit2.http.GET

interface FilmsRequest {
    @GET("films.json")
    fun films(): Call<FilmList>
}