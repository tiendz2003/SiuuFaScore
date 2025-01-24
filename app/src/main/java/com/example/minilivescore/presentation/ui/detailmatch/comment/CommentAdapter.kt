package com.example.minilivescore.presentation.ui.detailmatch.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minilivescore.databinding.ItemCmtBinding
import com.example.minilivescore.extension.loadImage

class CommentAdapter :ListAdapter<LiveComment,CommentAdapter.CommentViewHolder>(DiffCallback()) {
    inner class CommentViewHolder(
        private val binding:ItemCmtBinding
    ):RecyclerView.ViewHolder(binding.root) {
        fun bind(cmt :LiveComment){
            binding.apply {
                userAvatar.loadImage(cmt.userImage)
                userName.text = cmt.userName
                commentText.text = cmt.comment
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder {
        val binding = ItemCmtBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffCallback:DiffUtil.ItemCallback<LiveComment>(){
    override fun areItemsTheSame(oldItem: LiveComment, newItem: LiveComment): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: LiveComment, newItem: LiveComment): Boolean {
       return oldItem == newItem
    }


}
