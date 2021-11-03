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

    private var filteredPosts = ArrayList(listPosts)
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
        val post: Post = filteredPosts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = filteredPosts.size

    fun setFilteredList(posts: ArrayList<Post>) {
        updatePosts(posts)
    }

    fun setList(posts: ArrayList<Post>, filter: String) {
        allPosts.clear()
        allPosts.addAll(posts)
        exampleFilter.filter(filter)
    }

    private fun updatePosts(posts: ArrayList<Post>) {
        val diffCallback = PostsDiffCallback(filteredPosts, posts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        filteredPosts.clear()
        filteredPosts.addAll(posts)
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
            val newFilteredPosts = getNewFilteredPosts(results)
            updatePosts(newFilteredPosts)
        }

        private fun getNewFilteredPosts(results: FilterResults?): ArrayList<Post> {
            return if (results == null) {
                getFilterPosts(filterString)
            } else {
                val resultPosts = results.values as ArrayList<Post>
                if (resultPosts.isEmpty()) {
                    getFilterPosts(filterString)
                } else {
                    resultPosts
                }
            }
        }

        private fun getFilterPosts(filter: String): ArrayList<Post> {
            val filteredList = ArrayList<Post>()
            if (filter.isEmpty()) {
                filteredList.addAll(allPosts)
            } else {
                allPosts.forEach {
                    if (it.title.contains(filter) || it.body.contains(filter))
                        filteredList.add(it)
                }
            }
            return filteredList
        }
    }
}