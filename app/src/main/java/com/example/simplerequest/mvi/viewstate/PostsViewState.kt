package com.example.simplerequest.mvi.viewstate

import com.example.simplerequest.main.model.Post

sealed class PostsViewState {
    object Start : PostsViewState()
    object Loading : PostsViewState()
    object Empty : PostsViewState()
    data class Success(val posts: List<Post>) : PostsViewState()
    data class Error(val error: String) : PostsViewState()
}