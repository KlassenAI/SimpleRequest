package com.example.simplerequest.view

import com.arellomobile.mvp.MvpView
import com.example.simplerequest.model.Post

interface PostView : MvpView {
    fun showPosts(posts: List<Post>)
    fun showEmptyMessage()
    fun showErrorMessage()
}