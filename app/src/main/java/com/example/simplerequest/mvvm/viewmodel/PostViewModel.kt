package com.example.simplerequest.mvvm.viewmodel

import androidx.lifecycle.*
import com.example.simplerequest.main.extensions.Extensions.Companion.filterPosts
import com.example.simplerequest.main.extensions.Extensions.Companion.log
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
    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching
    private val _filter = MutableLiveData("")
    val filter: LiveData<String> = _filter
    val filteredPosts = MediatorLiveData<ArrayList<Post>>().apply {
        addSource(posts) {
            value = filterPosts(it, filter.value ?: "")
        }
        addSource(filter) {
            value = filterPosts(posts.value, it)
        }
    }

    fun requestPosts() {

        service.requestPosts().enqueue(object : Callback<ArrayList<Post>?> {

            override fun onResponse(
                call: Call<ArrayList<Post>?>, response: Response<ArrayList<Post>?>
            ) {
                _posts.postValue(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Post>?>, t: Throwable?) {
                _posts.postValue(null)
            }
        })
    }

    fun setSelectedPost(post: Post) {
        _selectedPost.postValue(post)
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.postValue(isSearching)
    }

    fun setFilter(filter: String) {
        _filter.postValue(filter)
    }
}