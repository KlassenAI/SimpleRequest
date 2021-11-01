package com.example.simplerequest.main.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplerequest.databinding.PostItemBinding
import com.example.simplerequest.main.model.Post

class PostItemAdapter(
    listPosts: ArrayList<Post>,
    private val listener: OnPostClickListener
) : RecyclerView.Adapter<PostItemAdapter.PostViewHolder>(), Filterable {

    private var posts = ArrayList(listPosts)
    private var allPosts = ArrayList(listPosts)
    private var filterString = ""

    inner class PostViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                postItemUserId.text = post.userId.toString()
                postItemId.text = post.id.toString()
                postItemTitle.text = post.title
                postItemBody.text = post.body
                postItemCard.setOnClickListener {
                    listener.onPostClick(post)
                }
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

    fun setList(posts: ArrayList<Post>) {
        val diffCallback = PostsDiffCallback(this.posts, posts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.posts.clear()
        this.posts.addAll(posts)
        this.allPosts.clear()
        this.allPosts.addAll(posts)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getFilter(): Filter {
        return exampleFilter
    }

    private val exampleFilter: Filter = object : Filter() {

        override fun performFiltering(charSequence: CharSequence): FilterResults {

            filterString = charSequence.toString()

            val filteredList = getFilterPosts(filterString)

            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            val resultPosts = results.values as ArrayList<Post>
            val posts = if (resultPosts.isEmpty()) {
                getFilterPosts(filterString)
            } else {
                resultPosts
            }
            val diffCallback = PostsDiffCallback(this@PostItemAdapter.posts, posts)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            this@PostItemAdapter.posts.clear()
            this@PostItemAdapter.posts.addAll(posts)
            diffResult.dispatchUpdatesTo(this@PostItemAdapter)
        }
    }

    private fun getFilterPosts(filter: String): ArrayList<Post> {
        val filteredList = ArrayList<Post>()
        if (filter.isEmpty()) {
            filteredList.addAll(allPosts)
        } else {
            for (item in allPosts) {
                if (item.title.contains(filter) || item.body.contains(filter)) {
                    filteredList.add(item)
                }
            }
        }
        return filteredList
    }
}