package com.example.simplerequest.main.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://jsonplaceholder.typicode.com/"

    val service: PostApiService by lazy {
        retrofit().create(PostApiService::class.java)
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}