package com.example.simplerequest.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.presenter.PostPresenter
import com.example.simplerequest.R
import com.example.simplerequest.databinding.ActivityMainBinding
import com.example.simplerequest.model.Post

class MainActivity : AppCompatActivity(), PostView {

    companion object {
        private const val TAG = "MainActivity"
    }


    private lateinit var binding: ActivityMainBinding
    private var presenter: PostPresenter = PostPresenter(this)
    private var adapter: PostItemAdapter = PostItemAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            recycler.layoutManager = LinearLayoutManager(applicationContext)
            recycler.adapter = adapter

            button.setOnClickListener {
                presenter.requestPosts()
            }
        }
    }

    override fun showPosts(posts: List<Post>) {
        adapter.setList(posts)
        Log.d(TAG, posts.toString())
    }

    override fun showEmptyMessage() {
        // показать сообщение, если ничего не было найдено
        Log.d(TAG, "showEmptyMessage")
    }

    override fun showErrorMessage() {
        // показать сообщение, если есть проблемы с соединением к интернету
        Log.d(TAG, "showErrorMessage")
    }
}