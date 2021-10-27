package com.example.simplerequest.mvvm.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.databinding.FragmentMvvmBinding
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
        }

        viewModel.posts.observe(viewLifecycleOwner, {
            when {
                it == null -> {
                    toast("Error")
                }
                it.isEmpty() -> {
                    toast("Empty")
                }
                else -> {
                    adapter.setList(it as ArrayList)
                }
            }
            binding.progressCircular.isVisible = false
        })
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    override fun onPostClick(post: Post) {
        TODO("Not yet implemented")
    }
}