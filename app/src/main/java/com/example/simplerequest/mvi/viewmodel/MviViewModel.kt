package com.example.simplerequest.mvi.viewmodel

import androidx.lifecycle.*
import com.example.simplerequest.main.extensions.Extensions
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvi.intent.PostIntent
import com.example.simplerequest.mvi.viewstate.PostListState
import com.example.simplerequest.mvi.viewstate.SelectPostState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviViewModel: ViewModel() {

    private val intentChannel = Channel<PostIntent>(Channel.UNLIMITED)
    private val _listState = MutableStateFlow<PostListState>(PostListState.Start)
    val listState: StateFlow<PostListState> = _listState
    private val _postState = MutableStateFlow<SelectPostState>(SelectPostState.Empty)
    val postState: StateFlow<SelectPostState> = _postState
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                when(it) {
                    is PostIntent.SelectPost -> setSelectedPost(it.post)
                    PostIntent.LoadPostsClick -> requestPosts()
                }
            }
        }
    }

    fun onIntent(postIntent: PostIntent) {
        viewModelScope.launch {
            intentChannel.send(postIntent)
        }
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.value = isSearching
    }

    private fun setSelectedPost(post: Post) {
        _postState.value = SelectPostState.Success(post)
        requestPost(post.id)
    }

    private fun requestPost(id: Int) {
        viewModelScope.launch {
            try {
                val deferredResponse = async { service.requestPostAsync(id) }
                val response = deferredResponse.await()
                if (response.isSuccessful) {
                    Extensions.log("response post ${response.body()}")
                } else {
                    Extensions.log("response post ${null}")
                }
            } catch (e: Exception) {
                Extensions.log("response post ${null}")
            }
        }
    }

    private fun requestPosts() {
        viewModelScope.launch {
            _listState.value = PostListState.Loading
            val response = service.requestPosts()
            if (response.isSuccessful) {
                val posts = response.body()!!
                if (posts.isEmpty()) {
                    _listState.value = PostListState.Empty
                } else {
                    _listState.value = PostListState.Success(posts)
                }
            } else {
                _listState.value = PostListState.Error(response.message())
            }
        }
    }
}