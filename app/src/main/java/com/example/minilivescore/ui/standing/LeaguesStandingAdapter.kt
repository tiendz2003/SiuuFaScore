package com.example.minilivescore.ui.standing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.data.model.LeagueMatches
import com.example.minilivescore.data.model.LeaguesStanding
import com.example.minilivescore.databinding.StandingLayoutItemBinding
import okhttp3.Request

class LeaguesStandingAdapter(
   val request: RequestManager
):ListAdapter<LeaguesStanding.Standing.Table,LeaguesStandingAdapter.ViewHolderLeague>(LeagueDiffCallBack()) {
   inner class ViewHolderLeague(private val binding :StandingLayoutItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(standing:LeaguesStanding.Standing.Table){
            binding.teamName.text = standing.team.tla
            binding.no.text = standing.position.toString()
            binding.played.text = standing.playedGames.toString()
            binding.wins.text = standing.won.toString()
            binding.draws.text = standing.draw.toString()
            binding.loses.text = standing.lost.toString()
            binding.goals.text = standing.goalsFor.toString()
            binding.points.text = standing.points.toString()

            request.load(standing.team.crest)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.logo)
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
    override fun areItemsTheSame(oldItem: LeaguesStanding.Standing.Table, newItem:LeaguesStanding.Standing.Table): Boolean {
        return oldItem.team.shortName == newItem.team.shortName
    }

    override fun areContentsTheSame(oldItem: LeaguesStanding.Standing.Table, newItem: LeaguesStanding.Standing.Table): Boolean {
        return oldItem == newItem
    }

}