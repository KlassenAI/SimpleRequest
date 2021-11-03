package com.example.simplerequest.mvvm.viewmodel

import androidx.lifecycle.*
import com.example.simplerequest.main.extensions.filterPosts
import com.example.simplerequest.main.extensions.log
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.service.RetrofitClient.service
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _posts = MutableLiveData<ArrayList<Post>?>()
    val posts: LiveData<ArrayList<Post>?> = _posts
    private val _selectedPost = MutableLiveData<Post?>()
    val selectedPost: LiveData<Post?> = _selectedPost
    private val _isSearching = MutableLiveData(false)
    val isSearching: LiveData<Boolean> = _isSearching
    private val _filter = MutableLiveData("")
    val filteredPosts = MediatorLiveData<ArrayList<Post>>().apply {
        val filterFunc = {
            val filter = _filter.value!!
            val posts = _posts.value
            value = filterPosts(posts, filter)
        }
        addSource(posts) { filterFunc() }
        addSource(_filter) { filterFunc() }
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
                val posts = service.requestPosts()
                _posts.postValue(posts)
            } catch (e: Exception) {
                _posts.postValue(null)
            }
        }
    }

    fun requestPost(id: Int) {
        viewModelScope.launch {
            try {
                val deferredResponse = async { service.requestPostAsync(id) }
                val post = deferredResponse.await()
                _selectedPost.postValue(post)
            } catch (e: Exception) {
                _selectedPost.postValue(null)
            }
        }
    }
}