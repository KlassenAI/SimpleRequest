package com.example.simplerequest.mvi.intent

import com.example.simplerequest.main.model.Post

sealed class PostIntent {
    object LoadPostsClick : PostIntent()
    data class SelectPost(val post: Post) : PostIntent()
    data class SaveKeyboardState(val focused: Boolean) : PostIntent()
}