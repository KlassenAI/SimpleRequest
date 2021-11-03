package com.example.simplerequest.mvp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.extensions.*
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvp.presenter.PostPresenter

class MvpFragment : MvpAppCompatFragment(), PostView, OnPostClickListener {

    @InjectPresenter
    lateinit var presenter: PostPresenter
    private lateinit var binding: FragmentMvpBinding
    private var adapter = PostItemAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                progressCircular.isVisible = adapter.itemCount == 0
                presenter.requestPosts()
            }

            filterEditText.setOnDefaultEditorActionListener()

            filterEditText.addTextChangedListenerWithDebounce {
                filterPosts(it)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showKeyboard(presenter.isSearching)
    }

    private fun showKeyboard(isSearching: Boolean) {
        activity?.showKeyboard(binding.filterEditText, isSearching)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isKeyboardShown = isKeyboardShown(activity?.findViewById(R.id.main_activity))
        presenter.isSearching = isKeyboardShown
    }

    override fun showPosts(posts: ArrayList<Post>) {
        adapter.setList(posts, binding.filterEditText.text.toString())
        binding.filterEditText.isEnabled = true
        binding.progressCircular.isVisible = false
    }

    override fun showEmptyMessage() {
        toast("Загружены пустые данные")
        binding.filterEditText.isEnabled = adapter.itemCount > 0
        binding.progressCircular.isVisible = false
    }

    override fun showErrorMessage() {
        toast("Ошибка загрузки")
        binding.filterEditText.isEnabled = adapter.itemCount > 0
        binding.progressCircular.isVisible = false
    }

    override fun showSelectedPost(post: Post?) {
        if (post != null) {
            binding.title.text = post.title
            binding.image.loadImage(post.id)
        }
    }

    override fun onPostClick(post: Post) {
        presenter.requestPost(post.id)
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }
}
