package com.example.simplerequest.main.service

import com.example.simplerequest.main.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostApiService {

    @GET("posts")
    suspend fun requestPosts(): Response<ArrayList<Post>?>

    @GET("posts/{id}")
    suspend fun requestPostAsync(@Path("id") id: Int): Response<Post>
}