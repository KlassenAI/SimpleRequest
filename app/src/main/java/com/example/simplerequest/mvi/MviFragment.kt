package com.example.simplerequest.mvi

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.databinding.FragmentMviBinding
import com.example.simplerequest.main.view.IFragmentListener
import com.example.simplerequest.main.view.ISearch
import com.example.simplerequest.main.view.PostItemAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviFragment : Fragment(), ISearch {

    companion object {
        private val TAG = MviFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentMviBinding
    private lateinit var viewModel: MviViewModel
    private var adapter = PostItemAdapter(listOf())
    private var mIFragmentListener: IFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMviBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MviViewModel::class.java)

        adapter = PostItemAdapter(listOf())

        observeViewModel()

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.postIntent.send(PostIntent.LoadPostsClick)
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when(it) {
                    PostState.Start -> {
                        log("Start")
                    }
                    PostState.Loading -> {
                        log("Loading")
                        binding.progressCircular.isVisible = true
                    }
                    PostState.Empty -> {
                        log("Empty")
                        binding.progressCircular.isVisible = false
                    }
                    is PostState.Error -> {
                        log("Error")
                        binding.progressCircular.isVisible = false
                    }
                    is PostState.Loaded -> {
                        log("Loaded")
                        adapter.setList(it.posts)
                        binding.progressCircular.isVisible = false
                    }
                }
            }
        }
    }

    private fun toast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, length).show()
    }

    override fun onTextQuery(text: String) {
        lifecycleScope.launch {
            viewModel.postIntent.send(PostIntent.SearchPost(text))
        }
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