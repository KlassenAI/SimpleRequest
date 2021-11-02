package com.example.simplerequest.mvi.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMviBinding
import com.example.simplerequest.main.extensions.Extensions
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.extensions.Extensions.Companion.showKeyboard
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvi.viewmodel.MviViewModel
import com.example.simplerequest.mvi.intent.PostIntent.LoadPostsClick
import com.example.simplerequest.mvi.intent.PostIntent.SelectPost
import com.example.simplerequest.mvi.viewstate.KeyboardState
import com.example.simplerequest.mvi.viewstate.PostListState
import com.example.simplerequest.mvi.viewstate.SelectPostState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviFragment : Fragment(), OnPostClickListener {

    private lateinit var binding: FragmentMviBinding
    private lateinit var viewModel: MviViewModel
    private var adapter = PostItemAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMviBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MviViewModel::class.java)

        observeListState()
        observePostState()

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                (viewModel::onIntent)(LoadPostsClick)
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
                        delay(500)
                        if (searchText != searchFor)
                            return@launch
                        filterPosts(s.toString())
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) = Unit

                override fun afterTextChanged(s: Editable?) = Unit

            })
        }
    }

    override fun onPostClick(post: Post) {
        (viewModel::onIntent)(SelectPost(post))
    }

    private fun observeListState() {
        lifecycleScope.launch {
            viewModel.listState.collect {
                when (it) {
                    PostListState.Start -> {
                        log("Start")
                    }
                    PostListState.Loading -> {
                        log("Loading")
                        binding.progressCircular.isVisible = true
                    }
                    PostListState.Empty -> {
                        log("Empty")
                        binding.progressCircular.isVisible = false
                        binding.filterEditText.isEnabled = false
                    }
                    is PostListState.Error -> {
                        log("Error")
                        binding.progressCircular.isVisible = false
                        binding.filterEditText.isEnabled = false
                    }
                    is PostListState.Success -> {
                        log("Loaded")
                        adapter.setList(it.posts as ArrayList<Post>)
                        binding.progressCircular.isVisible = false
                        binding.filterEditText.isEnabled = true
                    }
                }
            }
        }
    }

    private fun observePostState() {
        lifecycleScope.launch {
            viewModel.postState.collect {
                when (it) {
                    SelectPostState.Empty -> {}
                    is SelectPostState.Success -> {
                        binding.apply {
                            title.text = it.post.title
                            image.loadImage(it.post.id)
                        }
                    }
                }
            }
        }
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isKeyboardShown = Extensions.isKeyboardShown(activity?.findViewById(R.id.main_activity))
        viewModel.setIsSearching(isKeyboardShown)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showKeyboard(viewModel.isSearching.value)
    }

    private fun showKeyboard(isSearching: Boolean) {
        activity?.showKeyboard(binding.filterEditText, isSearching)
    }
}