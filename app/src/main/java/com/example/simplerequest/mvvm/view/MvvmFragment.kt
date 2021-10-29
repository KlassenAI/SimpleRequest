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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.databinding.FragmentMvvmBinding
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.showKeyboard
import com.example.simplerequest.main.extensions.Extensions.Companion.toast
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvvm.viewmodel.PostViewModel

class MvvmFragment : Fragment(), OnPostClickListener {

    private lateinit var binding: FragmentMvvmBinding
    private lateinit var viewModel: PostViewModel
    private var adapter = PostItemAdapter(arrayListOf(), this)

    companion object {
        private val TAG = MvvmFragment::class.java.simpleName
    }

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

            viewModel.posts.observe(viewLifecycleOwner, {
                when {
                    it == null -> {
                        toast("Error")
                        filterEditText.isEnabled = false
                    }
                    it.isEmpty() -> {
                        toast("Empty")
                        filterEditText.isEnabled = false
                    }
                    else -> {
                        adapter.setList(it)
                        filterEditText.isEnabled = true
                    }
                }
                progressCircular.isVisible = false
            })

            viewModel.selectedPost.observe(viewLifecycleOwner, {
                title.text = it?.title
                image.loadImage(it?.id)
            })

            viewModel.keyboardState.observe(viewLifecycleOwner, {
                showKeyboard(filterEditText, requireActivity())
            })

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
        viewModel.saveSelectedPost(post)
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveKeyboardState(binding.filterEditText.isFocused)
    }
}