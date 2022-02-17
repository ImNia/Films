package com.delirium.films.genres

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Common {
    private const val BASE_URL = "https://s3-eu-west-1.amazonaws.com/sequeniatesttask/"
    val filmsRequest: FilmsRequest
        get() = RetrofitClient.getClient(BASE_URL).create(FilmsRequest::class.java)
}

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}