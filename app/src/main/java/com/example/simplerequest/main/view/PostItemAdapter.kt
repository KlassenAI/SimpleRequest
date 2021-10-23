package com.example.simplerequest.main.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerequest.databinding.PostItemBinding
import com.example.simplerequest.main.model.Post

class PostItemAdapter(
    private var posts: List<Post>
) : RecyclerView.Adapter<PostItemAdapter.PostViewHolder>() {

    inner class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                postItemUserId.text = post.userId.toString()
                postItemId.text = post.id.toString()
                postItemTitle.text = post.title
                postItemBody.text = post.body
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemBinding: PostItemBinding =
            PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: Post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    fun setList(list: List<Post>) {
        posts = list
        notifyDataSetChanged()
    }
}