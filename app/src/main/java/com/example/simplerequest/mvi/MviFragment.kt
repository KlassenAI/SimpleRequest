package com.example.simplerequest.mvi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvi.PostIntent.LoadPostsClick
import com.example.simplerequest.mvi.PostIntent.SelectPost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviFragment : Fragment(), OnPostClickListener {

    companion object {
        private val TAG = MviFragment::class.java.simpleName
    }

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

            filterEditText.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterPosts(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) {}

                override fun afterTextChanged(s: Editable?) {}

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
}