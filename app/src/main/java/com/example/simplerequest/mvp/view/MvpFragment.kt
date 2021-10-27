package com.example.simplerequest.mvp.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.arellomobile.mvp.presenter.InjectPresenter
import com.bumptech.glide.Glide
import com.example.simplerequest.databinding.FragmentMvpBinding
import com.example.simplerequest.main.model.Post
import com.example.simplerequest.main.view.OnPostClickListener
import com.example.simplerequest.main.view.PostItemAdapter
import com.example.simplerequest.mvp.presenter.PostPresenter
import com.example.simplerequest.R
class MvpFragment : MvpAppCompatFragment(), PostView, OnPostClickListener {

    companion object {
        private val TAG = MvpFragment::class.java.simpleName
        private const val KEY_KEYBOARD = "keyboard"
        private const val KEY_POST = "post"
    }

    private lateinit var binding: FragmentMvpBinding

    @InjectPresenter
    lateinit var presenter: PostPresenter
    private var adapter = PostItemAdapter(arrayListOf(), this)
    private var post: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMvpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (post != null) outState.putParcelable(KEY_POST, post)
        outState.putBoolean(KEY_KEYBOARD, binding.editText.isFocused)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {

            val post: Post? = savedInstanceState.getParcelable(KEY_POST)
            if (post != null) {
                setSelectedPost(post)
            }

            val isShown = savedInstanceState.getBoolean(KEY_KEYBOARD)
            if (isShown) {
                showKeyboardForEditText(binding.editText)
            }
        }
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

            editText.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterPosts(s.toString())
                }

                override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) {}

                override fun afterTextChanged(s: Editable?) {}

            })
        }
    }

    private fun showKeyboardForEditText(editText: EditText) {
        val input = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        input.showSoftInput(editText, 0)
        editText.requestFocus()
    }

    override fun showPosts(posts: ArrayList<Post>) {
        log("Posts Size ${posts.size}")
        adapter.setList(posts)
        filterPosts(binding.editText.text.toString())
        binding.progressCircular.isVisible = false
        binding.editText.isEnabled = true
    }

    override fun showEmptyMessage() {
        log("empty")
        binding.progressCircular.isVisible = false
        binding.editText.isEnabled = false
    }

    override fun showErrorMessage() {
        log("error")
        binding.progressCircular.isVisible = false
        binding.editText.isEnabled = false
    }

    override fun showKeyboard(isShown: Boolean) {
        if (isShown) showKeyboardForEditText(binding.editText)
    }

    override fun onPostClick(post: Post) {
        log(post.toString())
        setSelectedPost(post)
    }

    private fun setSelectedPost(post: Post) {
        this.post = post
        binding.title.text = post.title
        binding.image.loadImage(post.id)
    }

    private fun filterPosts(filter: String) {
        adapter.filter.filter(filter)
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun log(text: String) {
        Log.d(TAG, text)
    }

    private fun ImageView.loadImage(id: Int) {
        Glide.with(context)
            .load("https://picsum.photos/id/$id/640/480")
            .placeholder(getPlaceholder(context))
            .error(R.drawable.posts)
            .into(this)
    }

    private fun getPlaceholder(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
    }
}

