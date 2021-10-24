package com.example.simplerequest.mvi

import com.example.simplerequest.main.model.Post

sealed class PostState {
    object Start : PostState()
    object Loading : PostState()
    object Empty : PostState()
    data class Loaded(val posts: List<Post>) : PostState()
    data class Error(val error: String) : PostState()
}