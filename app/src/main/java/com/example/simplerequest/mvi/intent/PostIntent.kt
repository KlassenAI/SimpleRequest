package com.example.simplerequest.mvi.intent

import com.example.simplerequest.main.model.Post

sealed class PostIntent {
    object LoadPosts : PostIntent()
    data class SelectPost(val post: Post) : PostIntent()
}