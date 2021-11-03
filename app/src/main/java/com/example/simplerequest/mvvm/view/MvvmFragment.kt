package com.example.simplerequest.mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMvvmBinding
import com.example.simplerequest.main.extensions.*
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvvm.viewmodel.PostViewModel

class MvvmFragment : Fragment(), OnPostClickListener {

    private lateinit var binding: FragmentMvvmBinding
    private lateinit var viewModel: PostViewModel
    private var adapter = PostItemAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvvmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        observeViewModel()

        binding.apply {

            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                progressCircular.isVisible = adapter.itemCount == 0
                viewModel.requestPosts()
            }

            filterEditText.setOnDefaultEditorActionListener()

            filterEditText.addTextChangedListenerWithDebounce {
                viewModel.setFilter(it)
            }
        }
    }

    private fun observeViewModel() {
        binding.apply {

            viewModel.selectedPost.observe(viewLifecycleOwner, {
                if (it != null) {
                    title.text = it.title
                    image.loadImage(it.id)
                }
            })

            viewModel.filteredPosts.observe(viewLifecycleOwner, {
                when {
                    it == null -> log("Ошибка загрузки")
                    it.isEmpty() -> log("Загружены пустые данные")
                    else -> adapter.setFilteredList(it)
                }
                filterEditText.isEnabled = adapter.itemCount > 0
                progressCircular.isVisible = false
            })
        }
    }

    override fun onPostClick(post: Post) {
        viewModel.requestPost(post.id)
        binding.scrollView.smoothScrollTo(0, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isKeyboardShown = isKeyboardShown(activity?.findViewById(R.id.main_activity))
        viewModel.setIsSearching(isKeyboardShown)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showKeyboard(viewModel.isSearching.value!!)
    }

    private fun showKeyboard(isSearching: Boolean) {
        activity?.showKeyboard(binding.filterEditText, isSearching)
    }
}