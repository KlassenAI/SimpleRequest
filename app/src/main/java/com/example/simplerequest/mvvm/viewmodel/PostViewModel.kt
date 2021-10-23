package com.example.simplerequest.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient
import com.example.simplerequest.mvvm.model.LoadState
import com.example.simplerequest.mvvm.model.LoadState.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts
    private val _loadState = MutableLiveData<LoadState>()
    val loadState: LiveData<LoadState> = _loadState

    fun requestPosts() {

        RetrofitClient.create().requestPosts().enqueue(object : Callback<List<Post>> {

            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val posts = response.body()
                if (posts.isNotEmpty()) {
                    _posts.postValue(posts)
                    _loadState.postValue(SUCCESS)
                } else {
                    _loadState.postValue(EMPTY)
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable?) {
                _loadState.postValue(ERROR)
            }
        })
    }
}