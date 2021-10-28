package com.example.simplerequest.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvp.view.PostView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@InjectViewState
class PostPresenter: MvpPresenter<PostView>() {

    fun saveSelectedPost(post: Post) {
        viewState.showSelectedPost(post)
    }

    fun requestPosts() {

        service.requestPosts().enqueue(object : Callback<ArrayList<Post>?> {

            override fun onResponse(call: Call<ArrayList<Post>?>, response: Response<ArrayList<Post>?>) {
                val posts = response.body()!!
                if (posts.isNotEmpty()) {
                    viewState.showPosts(posts)
                } else {
                    viewState.showEmptyMessage()
                }
            }

            override fun onFailure(call: Call<ArrayList<Post>?>, t: Throwable?) {
                viewState.showErrorMessage()
            }
        })
    }
}