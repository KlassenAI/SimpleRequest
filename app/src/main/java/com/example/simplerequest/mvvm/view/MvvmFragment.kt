package com.example.simplerequest.mvvm.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.R
import com.example.simplerequest.databinding.FragmentMviBinding
import com.example.simplerequest.databinding.FragmentMvvmBinding
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvvm.model.LoadState
import com.example.simplerequest.mvvm.model.LoadState.*
import com.example.simplerequest.mvvm.viewmodel.PostViewModel

class MvvmFragment : Fragment() {

    companion object {
        private const val TAG = "MvvmFragment"
    }

    private lateinit var binding: FragmentMvvmBinding
    private lateinit var viewModel: PostViewModel
    private var adapter: PostItemAdapter = PostItemAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvvmBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                viewModel.requestPosts()
            }
        }

        viewModel.posts.observe(viewLifecycleOwner, {
            adapter.setList(it)
        })

        viewModel.loadState.observe(viewLifecycleOwner, {
            Log.d(TAG, when(it) {
                SUCCESS -> "Success"
                EMPTY -> "Empty"
                ERROR -> "Error"
                else -> "null"
            })
        })
    }
}