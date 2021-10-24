package com.example.simplerequest.mvi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplerequest.databinding.FragmentMviBinding
import com.example.simplerequest.main.view.PostItemAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MviFragment : Fragment() {

    companion object {
        private val TAG = MviFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentMviBinding
    private lateinit var viewModel: MviViewModel
    private var adapter: PostItemAdapter = PostItemAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMviBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MviViewModel::class.java)

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
                        toast("Загрузка данных")
                    }
                    PostState.Empty -> {
                        log("Empty")
                    }
                    is PostState.Error -> {
                        log("Error")
                    }
                    is PostState.Loaded -> {
                        log("Loaded")
                        adapter.setList(it.posts)
                    }
                }
            }
        }
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    private fun toast(text: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, length).show()
    }
}