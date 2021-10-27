package com.example.simplerequest.main.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
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

    fun setList(list: ArrayList<Post>) {
        Log.d("setList", list.size.toString())
        posts = ArrayList(list)
        Log.d("setList", posts.size.toString())
        allPosts = ArrayList(list)
        Log.d("setList", allPosts.size.toString())
        notifyDataSetChanged()
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
            posts = if (resultPosts.isEmpty()) {
                getFilterPosts(filterString)
            } else {
                resultPosts
            }

            Log.d("posts", posts.size.toString())
            Log.d("allPosts", allPosts.size.toString())
            Log.d("filter", filterString)
            notifyDataSetChanged()
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