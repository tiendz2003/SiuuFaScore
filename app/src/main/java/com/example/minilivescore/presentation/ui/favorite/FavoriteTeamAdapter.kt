package com.example.minilivescore.presentation.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.minilivescore.R
import com.example.minilivescore.data.model.football.FavoriteTeam
import com.example.minilivescore.databinding.ItemFavoriteTeamBinding
import com.example.minilivescore.extension.loadImage

class FavoriteTeamAdapter(
    private val onNotification:(String,Boolean)->Unit,
    //truyền TeamId
    private val onItemClicked: (String) -> Unit
) :ListAdapter<FavoriteTeam,FavoriteTeamAdapter.TeamViewHolder>(DiffCallback()) {
    inner class TeamViewHolder(
        private val binding: ItemFavoriteTeamBinding,
    ): RecyclerView.ViewHolder(binding.root) {
        private var bellAnimation:Animation? = null
        fun bind(team : FavoriteTeam){
            binding.apply {
                teamLogo.loadImage(team.logo)
                teamName.text = team.name
                // Khởi tạo animation
                if (bellAnimation == null) {
                    bellAnimation = AnimationUtils.loadAnimation(root.context, R.anim.toggle_anim)
                }
                //Cập nhật trạng thái giao diện từ firestore
                updateNotificationIcon(team.notificationEnabled)

                notificationIcon.setOnClickListener {
                    val newNotificationState  = !team.notificationEnabled
                    onNotification(team.id,newNotificationState)
                    updateNotificationIcon(newNotificationState)
                }
                root.setOnClickListener {
                    onItemClicked(team.id)
                }
            }
        }
        private fun updateNotificationIcon(enabled: Boolean) {
            binding.notificationIcon.apply {
                if(enabled){
                    setImageResource(R.drawable.baseline_notifications_active_24)
                    startAnimation(bellAnimation)
                }else{
                    clearAnimation()
                    setImageResource(R.drawable.notification)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteTeamAdapter.TeamViewHolder {
        val binding = ItemFavoriteTeamBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TeamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteTeamAdapter.TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffCallback: DiffUtil.ItemCallback<FavoriteTeam>(){
    override fun areItemsTheSame(oldItem: FavoriteTeam, newItem: FavoriteTeam): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FavoriteTeam, newItem: FavoriteTeam): Boolean {
        return oldItem == newItem
    }

}
