package com.example.simplerequest.mvi.viewstate

import com.example.simplerequest.main.model.Post

data class MapOrdersViewState(
    val isSearching: Boolean = false,
    val selectedPost: Post? = null,
    val postsViewState: PostsViewState = PostsViewState.Start
)
