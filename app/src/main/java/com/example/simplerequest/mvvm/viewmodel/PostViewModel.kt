package com.example.simplerequest.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel : ViewModel() {

    private val _posts = MutableLiveData<ArrayList<Post>?>()
    val posts: LiveData<ArrayList<Post>?> = _posts
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost

    fun requestPosts() {

        service.requestPosts().enqueue(object : Callback<ArrayList<Post>?> {

            override fun onResponse(call: Call<ArrayList<Post>?>, response: Response<ArrayList<Post>?>) {
                _posts.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Post>?>, t: Throwable?) {
                _posts.postValue(null)
            }
        })
    }

    fun saveSelectedPost(post: Post) {
        _selectedPost.postValue(post)
    }
}