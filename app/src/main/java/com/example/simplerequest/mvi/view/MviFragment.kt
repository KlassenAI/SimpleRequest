package com.example.simplerequest.mvi.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMviBinding
import com.example.simplerequest.main.extensions.*
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvi.intent.PostIntent.*
import com.example.simplerequest.mvi.viewmodel.MviViewModel
import com.example.simplerequest.mvi.viewstate.PostsViewState
import com.example.simplerequest.mvi.viewstate.PostsViewState.Loading
import com.example.simplerequest.mvi.viewstate.PostsViewState.Success
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

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

        observeViewState()

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                (viewModel::onIntent)(LoadPosts)
            }

            filterEditText.setOnDefaultEditorActionListener()

            filterEditText.addTextChangedListenerWithDebounce {
                filterPosts(it)
            }
        }
    }

    override fun onPostClick(post: Post) {
        (viewModel::onIntent)(SelectPost(post))
        binding.scrollView.smoothScrollTo(0, 0)
    }

    private fun observeViewState() {
        viewModel.mapOrdersViewState.onEach {
            editPostsViewState(it.postsViewState)
            setSelectedPost(it.selectedPost)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun editPostsViewState(postsViewState: PostsViewState) {
        when (postsViewState) {
            is Success -> {
                adapter.setList(
                    postsViewState.posts as ArrayList<Post>,
                    binding.filterEditText.text.toString()
                )
            }
            else -> log(postsViewState.toString())
        }
        binding.filterEditText.isEnabled = adapter.itemCount > 0 || postsViewState is Success
        binding.progressCircular.isVisible = postsViewState is Loading && adapter.itemCount == 0
    }

    private fun setSelectedPost(post: Post?) {
        if (post != null) {
            binding.title.text = post.title
            binding.image.loadImage(post.id)
        }
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isKeyboardShown = isKeyboardShown(activity?.findViewById(R.id.main_activity))
        viewModel.setIsSearching(isKeyboardShown)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showKeyboard(viewModel.mapOrdersViewState.value.isSearching)
    }

    private fun showKeyboard(isSearching: Boolean) {
        activity?.showKeyboard(binding.filterEditText, isSearching)
    }
}
