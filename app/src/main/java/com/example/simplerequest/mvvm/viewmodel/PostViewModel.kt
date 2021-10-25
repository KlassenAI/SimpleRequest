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

    private val _posts = MutableLiveData<List<Post>?>()
    val posts: LiveData<List<Post>?> = _posts

    fun requestPosts() {

        service.requestPosts().enqueue(object : Callback<List<Post>?> {

            override fun onResponse(call: Call<List<Post>?>, response: Response<List<Post>?>) {
                _posts.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Post>?>, t: Throwable?) {
                _posts.postValue(null)
            }
        })
    }

    fun searchPost(id: String) {

        service.searchPost(id).enqueue(object : Callback<Post?> {
            override fun onResponse(call: Call<Post?>, response: Response<Post?>) {
                val post = response.body()
                _posts.postValue(listOf(response.body()!!))
                if (post == null || post.id.toString() == "") {
                    _posts.postValue(listOf())
                } else {
                    _posts.postValue(listOf(post))
                }
            }

            override fun onFailure(call: Call<Post?>, t: Throwable?) {
                _posts.postValue(null)
            }
        })
    }
}