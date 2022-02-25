package com.delirium.films.model

import android.util.Log
import com.delirium.films.genres.Common
import com.delirium.films.genres.FilmsRequest
import com.delirium.films.genres.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Model(val presenter: Presenter) {
    private val filmRequest: FilmsRequest = Common.filmsRequest

    var requestData : List<FilmInfo> = listOf()

    fun getData() {
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                presenter.responseOnFailure()
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                requestData = response.body()?.films as List<FilmInfo>
                Log.i("MODEL", "Data get")
                presenter.changeDataForView()
            }
        })
    }
}