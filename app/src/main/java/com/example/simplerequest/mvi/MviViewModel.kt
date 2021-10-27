package com.example.simplerequest.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient
import com.example.simplerequest.main.service.RetrofitClient.service
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
class MviViewModel: ViewModel() {

    val postIntent = Channel<PostIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<PostState>(PostState.Start)
    val state: StateFlow<PostState>
        get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            postIntent.consumeAsFlow().collect {
                when(it) {
                    PostIntent.LoadPostsClick -> requestPosts()
                }
            }
        }
    }

    private fun requestPosts() {

        _state.value = PostState.Loading

        service.requestPosts().enqueue(object : Callback<ArrayList<Post>?> {

            override fun onResponse(call: Call<ArrayList<Post>?>, response: Response<ArrayList<Post>?>) {
                val posts = response.body()!!
                if (posts.isEmpty()) {
                    _state.value = PostState.Empty
                } else {
                    _state.value = PostState.Loaded(posts)
                }
            }

            override fun onFailure(call: Call<ArrayList<Post>?>, t: Throwable?) {
                _state.value = PostState.Error(t?.message.toString())
            }
        })
    }
}