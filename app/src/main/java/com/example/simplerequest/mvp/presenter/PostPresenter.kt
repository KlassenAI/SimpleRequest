package com.example.simplerequest.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvp.view.PostView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@InjectViewState
class PostPresenter: MvpPresenter<PostView>() {

    fun requestPosts() {

        service.requestPosts().enqueue(object : Callback<List<Post>?> {

            override fun onResponse(call: Call<List<Post>?>, response: Response<List<Post>?>) {
                val posts = response.body()!!
                if (posts.isEmpty()) {
                    viewState.showEmptyMessage()
                } else {
                    viewState.showPosts(posts)
                }
            }

            override fun onFailure(call: Call<List<Post>?>, t: Throwable?) {
                viewState.showErrorMessage()
            }
        })
    }

    fun searchPost(id: String) {

        service.searchPost(id).enqueue(object : Callback<Post?> {
            override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                val post = response.body()
                if (post == null || post.id.toString() == "") {
                    viewState.showEmptyMessage()
                } else {
                    viewState.showPosts(listOf(post))
                }
            }

            override fun onFailure(call: Call<Post?>, t: Throwable?) {
                viewState.showErrorMessage()
            }
        })
    }
}