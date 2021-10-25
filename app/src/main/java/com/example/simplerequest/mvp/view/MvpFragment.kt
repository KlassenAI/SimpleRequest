package com.example.simplerequest.mvp.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.IFragmentListener
import com.example.simplerequest.main.view.ISearch
import com.example.simplerequest.mvp.presenter.PostPresenter
import com.example.simplerequest.main.view.PostItemAdapter

class MvpFragment : MvpAppCompatFragment(), PostView, ISearch {

    companion object {
        private val TAG = MvpFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentMvpBinding
    @InjectPresenter
    lateinit var presenter: PostPresenter
    private var adapter = PostItemAdapter(listOf())
    private var mIFragmentListener: IFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostItemAdapter(listOf())

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                progressCircular.isVisible = true
                presenter.requestPosts()
            }
        }
    }

    override fun showPosts(posts: List<Post>) {
        adapter.setList(posts)
        binding.progressCircular.isVisible = false
        Log.d(TAG, posts.toString())
    }

    override fun showEmptyMessage() {
        Log.d(TAG, "showEmptyMessage")
        binding.progressCircular.isVisible = false
    }

    override fun showErrorMessage() {
        Log.d(TAG, "showErrorMessage")
        binding.progressCircular.isVisible = false
    }

    override fun onTextQuery(text: String) {
        binding.progressCircular.isVisible = true
        presenter.searchPost(text)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener?.addiSearch(this)
    }

    override fun onDetach() {
        super.onDetach()
        mIFragmentListener?.removeISearch(this)
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }
}