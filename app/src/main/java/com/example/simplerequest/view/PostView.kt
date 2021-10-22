package com.example.simplerequest.view

import com.example.simplerequest.model.Post

interface PostView {
    fun showPosts(posts: List<Post>)
    fun showEmptyMessage()
    fun showErrorMessage()
}