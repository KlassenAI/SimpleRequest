package com.example.simplerequest.mvp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvp.presenter.PostPresenter
import com.example.simplerequest.main.extensions.Extensions.Companion.isKeyboardShown
import com.example.simplerequest.main.extensions.Extensions.Companion.showKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MvpFragment : MvpAppCompatFragment(), PostView, OnPostClickListener {

    private lateinit var binding: FragmentMvpBinding

    @InjectPresenter
    lateinit var presenter: PostPresenter
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
                progressCircular.isVisible = true
                presenter.requestPosts()
            }

            filterEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    filterEditText.clearFocus()
                }
                false
            }

            filterEditText.addTextChangedListener(object : TextWatcher {

                private var searchFor = ""

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val searchText = s.toString()
                    if (searchText == searchFor) return
                    searchFor = searchText
                    lifecycleScope.launch {
                        delay(300)
                        if (searchText != searchFor)
                            return@launch
                        filterPosts(searchFor)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) = Unit
                override fun afterTextChanged(s: Editable?) = Unit
            })
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
        val isShown = isKeyboardShown(activity?.findViewById(R.id.main_activity))
        presenter.isSearching = isShown
    }

    override fun showPosts(posts: ArrayList<Post>) {
        log("success")
        adapter.setList(posts)
        filterPosts(binding.filterEditText.text.toString())
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = true
    }

    override fun showEmptyMessage() {
        log("empty")
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = false
    }

    override fun showErrorMessage() {
        log("error")
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = false
    }

    override fun showSelectedPost(post: Post) {
        binding.title.text = post.title
        binding.image.loadImage(post.id)
    }

    override fun onPostClick(post: Post) {
        log(post.toString())
        presenter.saveSelectedPost(post)
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }
}
