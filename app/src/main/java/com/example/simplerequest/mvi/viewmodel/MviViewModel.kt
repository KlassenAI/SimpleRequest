package com.example.simplerequest.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import com.example.simplerequest.mvi.intent.PostIntent
import com.example.simplerequest.mvi.viewstate.KeyboardState
import com.example.simplerequest.mvi.viewstate.PostListState
import com.example.simplerequest.mvi.viewstate.SelectPostState
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

    private val intentChannel = Channel<PostIntent>(Channel.UNLIMITED)
    private val _listState = MutableStateFlow<PostListState>(PostListState.Start)
    val listState: StateFlow<PostListState> = _listState
    private val _postState = MutableStateFlow<SelectPostState>(SelectPostState.Empty)
    val postState: StateFlow<SelectPostState> = _postState
    private val _keyboardState = MutableStateFlow<KeyboardState>(KeyboardState.isHidden)
    val keyboardState: StateFlow<KeyboardState> = _keyboardState

    init {
        handleIntent()
    }

    fun onIntent(postIntent: PostIntent) {
        viewModelScope.launch {
            intentChannel.send(postIntent)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect {
                when(it) {
                    is PostIntent.SelectPost -> saveSelectPost(it.post)
                    PostIntent.LoadPostsClick -> requestPosts()
                    is PostIntent.SaveKeyboardState -> saveKeyboardState(it.focused)
                }
            }
        }
    }

    private fun saveKeyboardState(focused: Boolean) {
        _keyboardState.value = if (focused) KeyboardState.isShown else KeyboardState.isHidden
    }

    private fun saveSelectPost(post: Post) {
        _postState.value = SelectPostState.Success(post)
    }

    private fun requestPosts() {

        _listState.value = PostListState.Loading

        service.requestPosts().enqueue(object : Callback<ArrayList<Post>?> {

            override fun onResponse(call: Call<ArrayList<Post>?>, response: Response<ArrayList<Post>?>) {
                val posts = response.body()!!
                if (posts.isEmpty()) {
                    _listState.value = PostListState.Empty
                } else {
                    _listState.value = PostListState.Success(posts)
                }
            }

            override fun onFailure(call: Call<ArrayList<Post>?>, t: Throwable?) {
                _listState.value = PostListState.Error(t?.message.toString())
            }
        })
    }
}