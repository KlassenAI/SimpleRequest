package com.example.simplerequest.main.service

import com.example.simplerequest.main.model.Post
import retrofit2.Call
import retrofit2.http.GET

interface PostApiService {

    @GET("posts")
    fun requestPosts(): Call<ArrayList<Post>?>
}