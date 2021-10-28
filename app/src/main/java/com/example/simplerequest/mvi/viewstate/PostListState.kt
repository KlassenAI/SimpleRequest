package com.example.simplerequest.mvi.viewstate

import com.example.simplerequest.main.model.Post

sealed class PostListState {
    object Start : PostListState()
    object Loading : PostListState()
    object Empty : PostListState()
    data class Success(val posts: List<Post>) : PostListState()
    data class Error(val error: String) : PostListState()
}