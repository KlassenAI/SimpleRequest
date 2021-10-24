package com.example.simplerequest.mvi

sealed class PostIntent {
    object LoadPostsClick: PostIntent()
}