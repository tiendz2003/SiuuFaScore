package com.example.minilivescore.presentation.ui.standing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.databinding.StandingLayoutItemBinding

class LeaguesStandingAdapter(
   val request: RequestManager
):ListAdapter<LeaguesStanding.Standing.Table, LeaguesStandingAdapter.ViewHolderLeague>(
    LeagueDiffCallBack()
) {
   inner class ViewHolderLeague(private val binding :StandingLayoutItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(standing: LeaguesStanding.Standing.Table){
            binding.apply {
                teamName.text = standing.team.tla
                no.text = String.format(standing.position.toString())
                played.text = String.format(standing.playedGames.toString())
                wins.text = String.format(standing.won.toString())
                draws.text = String.format(standing.draw.toString())
                loses.text = String.format(standing.lost.toString())
                goals.text = String.format(standing.goalsFor.toString())
                points.text = String.format(standing.points.toString())
                request.load(standing.team.crest)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(logo)
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderLeague {
        return ViewHolderLeague(
            StandingLayoutItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderLeague, position: Int) = holder.bind(getItem(position))
}
class LeagueDiffCallBack : DiffUtil.ItemCallback<LeaguesStanding.Standing.Table>(){
    override fun areItemsTheSame(oldItem: LeaguesStanding.Standing.Table, newItem: LeaguesStanding.Standing.Table): Boolean {
        return oldItem.team.shortName == newItem.team.shortName
    }

    override fun areContentsTheSame(oldItem: LeaguesStanding.Standing.Table, newItem: LeaguesStanding.Standing.Table): Boolean {
        return oldItem == newItem
    }

}