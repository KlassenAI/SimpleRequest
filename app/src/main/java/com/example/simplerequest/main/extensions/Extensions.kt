package com.example.simplerequest.main.extensions

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.simplerequest.R

class Extensions {

    companion object {

        private const val TAG = "TAG"

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
                start()
            }
        }

        fun Fragment.toast(text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun log(text: String, tag: String = TAG) {
            Log.d(TAG, text)
        }
    }
}