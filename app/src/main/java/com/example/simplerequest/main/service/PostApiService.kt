package com.example.simplerequest.main.service

import com.example.simplerequest.main.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {

    @GET("posts")
    fun requestPosts(): Call<List<Post>?>

    @GET("posts/{id}")
    fun searchPost(@Path("id") id: String): Call<Post?>
}