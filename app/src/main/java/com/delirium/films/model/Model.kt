package com.delirium.films.model

import com.delirium.films.films.SequeniaTestTaskSetting
import com.delirium.films.films.FilmsRequest
import com.delirium.films.films.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
import java.net.SocketTimeoutException
import java.net.UnknownServiceException

class Model(val presenter: Presenter) {
    private val filmRequest: FilmsRequest = SequeniaTestTaskSetting.filmsRequest

    var requestData : List<FilmInfo> = listOf()

    fun getData() {
        filmRequest.films().enqueue(object : Callback<FilmList> {
            override fun onFailure(call: Call<FilmList>, t: Throwable) {
                when (t) {
                    is SocketTimeoutException -> presenter.responseOnFailure(REQUEST_TIMEOUT)
                    is NumberFormatException,
                    is UnknownServiceException -> presenter.responseOnFailure(CONFLICT_VALUE)
                    else -> presenter.responseOnFailure()
                }
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<FilmList>,
                response: Response<FilmList>
            ) {
                if(response.code() == OK) {
                    requestData = response.body()?.films as List<FilmInfo>
                    presenter.loadData()
                } else if(response.code() == NOT_FOUND) {
                    presenter.responseOnFailure(response.code())
                }
            }
        })
    }

    companion object {
        const val NOT_FOUND = 404
        const val OK = 200
        const val REQUEST_TIMEOUT = 408
        const val CONFLICT_VALUE = 409
    }
}