package com.example.simplerequest.mvvm.view

import android.content.Context
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
import com.example.simplerequest.main.view.IFragmentListener
import com.example.simplerequest.main.view.ISearch
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvvm.viewmodel.PostViewModel

class MvvmFragment : Fragment(), ISearch {

    private lateinit var binding: FragmentMvvmBinding
    private lateinit var viewModel: PostViewModel
    private var adapter = PostItemAdapter(listOf())
    private var mIFragmentListener: IFragmentListener? = null

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

        adapter = PostItemAdapter(listOf())

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
                    adapter.setList(it)
                }
            }
            binding.progressCircular.isVisible = false
        })
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onTextQuery(text: String) {
        viewModel.searchPost(text)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIFragmentListener = context as IFragmentListener
        mIFragmentListener?.addiSearch(this)
    }

    override fun onDetach() {
        super.onDetach()
        mIFragmentListener?.removeISearch(this)
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }
}