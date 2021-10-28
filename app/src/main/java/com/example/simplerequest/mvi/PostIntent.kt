package com.example.simplerequest.mvi

import com.example.simplerequest.main.model.Post

sealed class PostIntent {
    object LoadPostsClick: PostIntent()
    data class SelectPost (val post: Post): PostIntent()
}