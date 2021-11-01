package com.example.simplerequest.mvvm.view

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
import com.example.simplerequest.databinding.FragmentMvvmBinding
import com.example.simplerequest.main.extensions.Extensions
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.extensions.Extensions.Companion.showKeyboard
import com.example.simplerequest.main.extensions.Extensions.Companion.toast
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvvm.viewmodel.PostViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                progressCircular.isVisible = true
                viewModel.requestPosts()
            }

            viewModel.selectedPost.observe(viewLifecycleOwner, {
                title.text = it?.title
                image.loadImage(it?.id)
            })

            viewModel.filteredPosts.observe(viewLifecycleOwner, {
                when {
                    it == null -> {
                        log("Error")
                        filterEditText.isEnabled = false
                    }
                    it.isEmpty() -> {
                        log("Empty")
                        filterEditText.isEnabled = false
                    }
                    else -> {
                        log("Success")
                        adapter.setList(it)
                        filterEditText.isEnabled = true
                    }
                }
                progressCircular.isVisible = false
            })

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
                        viewModel.setFilter(s.toString())
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) {}

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    override fun onPostClick(post: Post) {
        viewModel.setSelectedPost(post)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isShown = Extensions.isKeyboardShown(activity?.findViewById(R.id.main_activity))
        viewModel.setIsSearching(isShown)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        showKeyboard(viewModel.isSearching.value!!)
    }

    private fun showKeyboard(isSearching: Boolean) {
        activity?.showKeyboard(binding.filterEditText, isSearching)
    }
}