package com.example.minilivescore.ui.matches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.data.model.LeagueMatches
import com.example.minilivescore.data.model.Match
import com.example.minilivescore.databinding.ItemMatchBinding
import com.example.minilivescore.utils.Preferences

class MatchesAdapter(
    private val request: RequestManager,
    private val onItemClick:(LeagueMatches.Matche) ->  Unit
):ListAdapter<LeagueMatches.Matche,MatchesAdapter.MatchViewHolder> (MatchDiffCallBack()){
   inner class MatchViewHolder(private val binding: ItemMatchBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(match: LeagueMatches.Matche){
            binding.apply {
               statusFinish.visibility = View.GONE
               statusStart.visibility = View.GONE
                statusInplay.visibility = View.GONE
                homeTeamTextView.text = match.homeTeam.shortName
                awayTeamTextView.text = match.awayTeam.shortName
                // binding.statusStart.text = match.status
                when(match.status){
                    "FINISHED" ->{
                        statusFinish.visibility = View.VISIBLE
                    }
                    "TIMED"->{
                        statusStart.visibility = View.VISIBLE
                    }
                    else ->{
                        statusInplay.visibility = View.VISIBLE
                    }
                }
                scoreTextView.text ="${match.score.fullTime?.home?:"-"} - ${match.score.fullTime?.away?:"-"}"
                request.load(match.homeTeam.crest)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(homeTeam)
                request.load(match.awayTeam.crest)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(awayTeam)
                itemView.setOnClickListener {
                    it.isEnabled = false
                    onItemClick(match)
                    it.postDelayed({it.isEnabled = true},500)
                }
                Preferences.setFontStyle(statusStart)
                Preferences.setFontStyle(statusFinish)
                Preferences.setFontStyle(statusInplay)
            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MatchesAdapter.MatchViewHolder {
        return MatchViewHolder(
            ItemMatchBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: MatchesAdapter.MatchViewHolder, position: Int) {
       holder.bind(getItem(position))
    }

}

class MatchDiffCallBack :DiffUtil.ItemCallback<LeagueMatches.Matche>(){
    override fun areItemsTheSame(oldItem: LeagueMatches.Matche, newItem: LeagueMatches.Matche): Boolean {
        return oldItem.utcDate == newItem.utcDate
    }

    override fun areContentsTheSame(oldItem: LeagueMatches.Matche, newItem: LeagueMatches.Matche): Boolean {
        return oldItem == newItem
    }

}