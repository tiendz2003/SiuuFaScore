package com.example.minilivescore.ui.searchteam

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.data.model.TeamEntity
import com.example.minilivescore.databinding.ItemTeamBinding
import okhttp3.Request

class SearchTeamAdapter(
    private val request: RequestManager,
    private val onItemClick:(TeamEntity) -> Unit
    ):ListAdapter<TeamEntity,SearchTeamAdapter.TeamViewHolder>(TeamDiffCallback()) {
    inner class TeamViewHolder(private val binding:ItemTeamBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(team:TeamEntity){
            binding.apply {
                tvNameClub.text = team.name
                 request.load(team.crest)
                   .fitCenter()
                   .transition(DrawableTransitionOptions.withCrossFade())
                   .into(imgTeam)
                //Xử lý khi ấn vào 1 đội
                root.setOnClickListener {
                    onItemClick(team)
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTeamAdapter.TeamViewHolder {
        return TeamViewHolder(ItemTeamBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SearchTeamAdapter.TeamViewHolder, position: Int) {
        val team = getItem(position)
      //  Log.d("SearchTeamAdapter", "Binding team: ${team.name} at position $position")
        return holder.bind(team)
    }

    class TeamDiffCallback:DiffUtil.ItemCallback<TeamEntity>() {
        override fun areItemsTheSame(oldItem: TeamEntity, newItem: TeamEntity): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TeamEntity, newItem: TeamEntity): Boolean {
           return oldItem == newItem
        }

    }
}