package com.example.simplerequest.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvp.view.PostView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@InjectViewState
class PostPresenter: MvpPresenter<PostView>() {

    var isSearching = false

    fun saveSelectedPost(post: Post) {
        viewState.showSelectedPost(post)
    }

    fun requestPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.requestPosts()
            if (response.isSuccessful) {
                val posts = response.body()!!
                if (posts.isNotEmpty()) {
                    viewState.showPosts(posts)
                } else {
                    viewState.showEmptyMessage()
                }
            } else {
                viewState.showErrorMessage()
            }
        }
    }
}