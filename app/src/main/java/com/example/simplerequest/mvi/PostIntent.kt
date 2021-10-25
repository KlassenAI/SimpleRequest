package com.example.simplerequest.mvi

sealed class PostIntent {
    object LoadPostsClick: PostIntent()
    class SearchPost(val id: String): PostIntent()
}