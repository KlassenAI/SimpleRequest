package com.example.simplerequest.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.simplerequest.model.Post
import com.example.simplerequest.service.RetrofitClient
import com.example.simplerequest.view.PostView
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@InjectViewState
class PostPresenter: MvpPresenter<PostView>() {

    fun requestPosts() {

        RetrofitClient.create().requestPosts().enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val posts = response.body()
                if (posts.isEmpty()) {
                    viewState.showEmptyMessage()
                } else {
                    viewState.showPosts(posts)
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable?) {
                viewState.showErrorMessage()
            }
        })
    }
}