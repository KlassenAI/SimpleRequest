package com.example.simplerequest.mvp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.mvp.presenter.PostPresenter
import com.example.simplerequest.main.view.PostItemAdapter

class MvpFragment : MvpAppCompatFragment(), PostView {

    companion object {
        private const val TAG = "MvpFragment"
    }

    private lateinit var binding: FragmentMvpBinding
    @InjectPresenter
    lateinit var presenter: PostPresenter
    private var adapter: PostItemAdapter = PostItemAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
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