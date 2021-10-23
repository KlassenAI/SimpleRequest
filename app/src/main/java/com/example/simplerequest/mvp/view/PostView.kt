package com.example.simplerequest.mvp.view

import com.arellomobile.mvp.MvpView
import com.example.simplerequest.main.model.Post

interface PostView : MvpView {
    fun showPosts(posts: List<Post>)
    fun showEmptyMessage()
    fun showErrorMessage()
}