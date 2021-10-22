package com.example.simplerequest.service

import com.example.simplerequest.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface PostApiService {

    @GET("posts")
    fun requestPosts(): Call<List<Post>>
}