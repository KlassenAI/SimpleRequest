package com.example.simplerequest.mvp.view

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.RESULT_UNCHANGED_SHOWN
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.extensions.Extensions.Companion.loadImage
import com.example.simplerequest.main.extensions.Extensions.Companion.log
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvp.presenter.PostPresenter
import androidx.core.content.ContextCompat.getSystemService
import com.example.simplerequest.main.extensions.Extensions
import com.example.simplerequest.main.extensions.Extensions.Companion.showKeyboard


class MvpFragment : MvpAppCompatFragment(), PostView, OnPostClickListener {

    companion object {
        private val TAG = MvpFragment::class.java.simpleName
    }

    private lateinit var binding: FragmentMvpBinding

    @InjectPresenter
    lateinit var presenter: PostPresenter
    private var adapter = PostItemAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = adapter

            button.setOnClickListener {
                progressCircular.isVisible = true
                presenter.requestPosts()
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

    override fun onStop() {
        super.onStop()
        presenter.saveKeyboardState(binding.filterEditText.isFocused)
    }

    /*
    private fun showKeyboardForEditText(editText: EditText) {
        val input = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        input.showSoftInput(editText, 0)
        editText.requestFocus()
    }
     */

    override fun showPosts(posts: ArrayList<Post>) {
        log("Posts Size ${posts.size}")
        adapter.setList(posts)
        filterPosts(binding.filterEditText.text.toString())
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = true
    }

    override fun showEmptyMessage() {
        log("empty")
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = false
    }

    override fun showErrorMessage() {
        log("error")
        binding.progressCircular.isVisible = false
        binding.filterEditText.isEnabled = false
    }

    override fun showSelectedPost(post: Post) {
        binding.title.text = post.title
        binding.image.loadImage(post.id)
    }

    override fun showKeyboard() {
        showKeyboard(binding.filterEditText, requireActivity())
    }

    override fun onPostClick(post: Post) {
        log(post.toString())
        presenter.saveSelectedPost(post)
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }
}

