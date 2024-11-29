package com.example.minilivescore.presentation.ui.detailteam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minilivescore.data.model.football.PlayerEntity
import com.example.minilivescore.databinding.ItemPlayerBinding

class DetailTeamAdapter:ListAdapter<PlayerEntity, DetailTeamAdapter.PlayerViewHolder>(
    PLayerDiffCallBack()
) {
    inner class PlayerViewHolder(val binding:ItemPlayerBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(player: PlayerEntity){
            binding.apply {
                playerName.text = player.name
                playerPosition.text = player.position
                playerNationality.text = player.nationality
                playerPosition.text = player.position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        return PlayerViewHolder(
            ItemPlayerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
       return holder.bind(getItem(position))
    }
}

class PLayerDiffCallBack():DiffUtil.ItemCallback<PlayerEntity>(){
    override fun areItemsTheSame(oldItem: PlayerEntity, newItem: PlayerEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlayerEntity, newItem: PlayerEntity): Boolean {
       return oldItem == newItem
    }


}
