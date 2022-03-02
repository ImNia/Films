package com.delirium.films.model

import com.delirium.films.films.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class Model(val presenter: Presenter) {
    private val filmRequest: FilmsRequest = SequeniaTestTaskSetting.filmsRequest

    var requestData : List<FilmInfo> = listOf()

    fun getData() {
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException ->
                        presenter.responseOnFailure(StatusCode.REQUEST_TIMEOUT)
                    is NumberFormatException ->
                        presenter.responseOnFailure(StatusCode.CONFLICT_VALUE)
                    is UnknownHostException ->
                        presenter.responseOnFailure(StatusCode.NOT_CONNECT)
                    else -> presenter.responseOnFailure(StatusCode.SOME_ERROR)
                }
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                if(response.isSuccessful) {
                    requestData = response.body()?.films as List<FilmInfo>
                    presenter.loadData()
                } else {
                    presenter.responseOnFailure(StatusCode.NOT_FOUND)
                }
            }
        })
    }
}