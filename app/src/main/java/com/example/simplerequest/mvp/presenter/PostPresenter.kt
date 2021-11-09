package com.example.simplerequest.mvp.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvp.view.PostView
import kotlinx.coroutines.*

@InjectViewState
class PostPresenter: MvpPresenter<PostView>() {

    var isSearching = false

    fun requestPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val posts = service.requestPosts()
                withContext(Dispatchers.Main) {
                    if (posts.isNotEmpty()) {
                        viewState.showPosts(posts)
                    } else {
                        viewState.showEmptyMessage()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState.showErrorMessage()
                }
            }
        }
    }

    fun requestPost(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val post = service.requestPostAsync(id)
                withContext(Dispatchers.Main) {
                    viewState.showSelectedPost(post)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    viewState.showSelectedPost(null)
                }
            }
        }
    }
}