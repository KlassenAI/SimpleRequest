package com.example.simplerequest.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvi.intent.PostIntent
import com.example.simplerequest.mvi.viewstate.MapOrdersViewState
import com.example.simplerequest.mvi.viewstate.PostsViewState.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviViewModel : ViewModel() {

    private val intentChannel = Channel<PostIntent>(Channel.UNLIMITED)
    private val _mapOrdersViewState = MutableStateFlow(MapOrdersViewState())
    val mapOrdersViewState: StateFlow<MapOrdersViewState> = _mapOrdersViewState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                when (it) {
                    is PostIntent.SelectPost -> requestPost(it.post.id)
                    PostIntent.LoadPosts -> requestPosts()
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
        updateState {
            it.copy(isSearching = isSearching)
        }
    }

    private fun requestPost(id: Int) {
        viewModelScope.launch {
            try {
                val post = service.requestPostAsync(id)
                updateState {
                    it.copy(selectedPost = post)
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(selectedPost = null)
                }
            }
        }
    }

    private fun requestPosts() {
        viewModelScope.launch {
            try {
                updateState { it.copy(postsViewState = Loading) }
                val posts = service.requestPosts()
                updateState {
                    it.copy(postsViewState = if (posts.isEmpty()) Empty else Success(posts))
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(postsViewState = Error(e.message.toString()))
                }
            }
        }
    }

    private fun updateState(function: (state: MapOrdersViewState) -> MapOrdersViewState) {
        _mapOrdersViewState.value = function(_mapOrdersViewState.value)
    }
}