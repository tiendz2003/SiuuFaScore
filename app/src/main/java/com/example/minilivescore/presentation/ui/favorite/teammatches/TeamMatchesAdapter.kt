package com.example.minilivescore.presentation.ui.favorite.teammatches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.minilivescore.data.model.football.TeamMatches
import com.example.minilivescore.databinding.MatchListItemBinding
import com.example.minilivescore.extension.loadImage
import com.example.minilivescore.utils.Preferences

class TeamMatchesAdapter(
    private val request: RequestManager
): ListAdapter<TeamMatches.Matche,TeamMatchesAdapter.TeamMatchesViewHolder>(TeamMatchesDiffCallBack()) {
    class TeamMatchesViewHolder(
        private val binding : MatchListItemBinding
    ):RecyclerView.ViewHolder(binding.root) {
        fun bind(match: TeamMatches.Matche) {
            val matchTime = Preferences.setupTime(match.utcDate)
            binding.apply {
                timeLayout.timeOfMatch.text = matchTime
                homeTeamLayout.teamName.text = match.homeTeam.shortName
                homeTeamLayout.clubIcon.loadImage(match.homeTeam.crest)
                awayTeamLayout.teamName.text = match.awayTeam.shortName
                awayTeamLayout.clubIcon.loadImage(match.awayTeam.crest)
                homeScore.text = (match.score.fullTime.home?:"0").toString()
                awayScore.text = (match.score.fullTime.away?:"0").toString()
                timeLayout.currentMinute.text = Preferences.formatDate(match.utcDate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMatchesViewHolder {
       return TeamMatchesViewHolder(
           MatchListItemBinding.inflate(
               LayoutInflater.from(parent.context),
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: TeamMatchesViewHolder, position: Int) {
       holder.bind(getItem(position))
    }
}

class TeamMatchesDiffCallBack:DiffUtil.ItemCallback<TeamMatches.Matche>() {
    override fun areItemsTheSame(
        oldItem: TeamMatches.Matche,
        newItem: TeamMatches.Matche
    ): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TeamMatches.Matche,
        newItem: TeamMatches.Matche
    ): Boolean {
        return oldItem == newItem
    }


}
