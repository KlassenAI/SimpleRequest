package com.example.simplerequest.main.extensions

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.simplerequest.R
import com.example.simplerequest.main.model.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun ImageView.loadImage(id: Int?) {
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
        setColorSchemeColors(R.color.purple_700)
        start()
    }
}

fun Fragment.toast(text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun log(text: String, tag: String = "TAG") {
    Log.d(tag, text)
}

fun isKeyboardShown(view: View?): Boolean {
    val heightDiff = view?.rootView?.height?.minus(view.height)!!
    return heightDiff > dpToPx(view.context)
}

private fun dpToPx(context: Context): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200F, metrics)
}

fun Activity.showKeyboard(editText: EditText, isSearching: Boolean) {
    if (isSearching) {
        Handler(Looper.getMainLooper()).postDelayed({
            editText.requestFocus()
            val imm =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN)
            editText.setSelection(editText.text.length)
        }, 1)
    } else {
        editText.clearFocus()
    }
}

fun filterPosts(posts: ArrayList<Post>?, filter: String): ArrayList<Post> {
    val filteredList = ArrayList<Post>()
    if (posts != null) {
        if (filter.isEmpty()) {
            filteredList.addAll(posts)
        } else {
            for (item in posts) {
                if (item.title.contains(filter) || item.body.contains(filter)) {
                    filteredList.add(item)
                }
            }
        }
    }
    return filteredList
}

fun EditText.setOnDefaultEditorActionListener() {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            this.clearFocus()
        }
        false
    }
}

fun EditText.addTextChangedListenerWithDebounce(function: (filter: String) -> Unit) {

    this.addTextChangedListener(object : TextWatcher {

        private var lastSearch = ""

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newSearch = s.toString()
            if (newSearch == lastSearch) return
            lastSearch = newSearch
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                if (newSearch != lastSearch)
                    return@launch
                function(lastSearch)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, str: Int, cnt: Int, aft: Int) = Unit
        override fun afterTextChanged(s: Editable?) = Unit
    })
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope) {
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}
