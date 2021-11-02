package com.example.simplerequest.mvvm.viewmodel

import androidx.lifecycle.*
import com.example.simplerequest.main.extensions.Extensions.Companion.filterPosts
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _posts = MutableLiveData<ArrayList<Post>?>()
    val posts: LiveData<ArrayList<Post>?> = _posts
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost
    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching
    private val _filter = MutableLiveData("")
    private val filter: LiveData<String> = _filter
    val filteredPosts = MediatorLiveData<ArrayList<Post>>().apply {
        addSource(posts) {
            value = filterPosts(it, filter.value ?: "")
        }
        addSource(filter) {
            value = filterPosts(posts.value, it)
        }
    }

    fun setSelectedPost(post: Post) {
        _selectedPost.postValue(post)
        requestPost(post.id)
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.postValue(isSearching)
    }

    fun setFilter(filter: String) {
        _filter.postValue(filter)
    }

    fun requestPosts() {
        viewModelScope.launch {
            try {
                val response = service.requestPosts()
                if (response.isSuccessful) {
                    _posts.postValue(response.body())
                } else {
                    _posts.postValue(null)
                }
            } catch (e: Exception) {
                _posts.postValue(null)
            }
        }
    }

    private fun requestPost(id: Int) {
        viewModelScope.launch {
            try {
                val response = service.requestPostAsync(id)
                if (response.isSuccessful) {
                    log("response post ${response.body()}")
                } else {
                    log("response post ${null}")
                }
            } catch (e: Exception) {
                log("response post ${null}")
            }
        }
    }
}