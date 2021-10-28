package com.example.simplerequest.mvi.viewstate

import com.example.simplerequest.main.model.Post

sealed class SelectPostState {
    object Empty : SelectPostState()
    data class Success(val post: Post) : SelectPostState()
}