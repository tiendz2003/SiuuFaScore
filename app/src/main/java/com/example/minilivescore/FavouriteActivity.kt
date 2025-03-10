package com.example.minilivescore

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minilivescore.databinding.ActivityFavouriteBinding
import com.example.minilivescore.presentation.ui.favorite.FavoriteTeamAdapter
import com.example.minilivescore.presentation.ui.favorite.FavoriteViewModel
import com.example.minilivescore.presentation.ui.favorite.teammatches.TeamMatchesFragment
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouriteActivity : AppCompatActivity() {
    private val binding: ActivityFavouriteBinding by lazy(LazyThreadSafetyMode.NONE){
        ActivityFavouriteBinding.inflate(layoutInflater)
    }

    private lateinit var teamAdapter:FavoriteTeamAdapter
    private val viewModel by viewModels<FavoriteViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupRecycleView()
        observeViewModel()

    }
    private fun setupRecycleView(){
        teamAdapter = FavoriteTeamAdapter (onNotification = { teamId, enable ->
            viewModel.toggleNotification(teamId,enable)
            if(enable){
                Toast.makeText(this@FavouriteActivity, "Đã bật nhận thông báo", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@FavouriteActivity, "Đã hủy thông báo", Toast.LENGTH_SHORT).show()
            }
        },
            onItemClicked = { teamId -> navigateToTeamMatches(teamId) }
        )
        binding.favoriteTeamsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavouriteActivity)
            adapter = teamAdapter
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFavoriteTeam()
        }
    }
    private fun observeViewModel(){
        lifecycleScope.launch {
          repeatOnLifecycle(Lifecycle.State.STARTED){
              viewModel.favoriteTeams.collect{teams ->
                  when(teams){
                      is Resource.Success -> {
                          binding.progressBar.visibility = View.GONE
                          binding.swipeRefreshLayout.isRefreshing = false
                          teams.data?.let { teamAdapter.submitList(it) }?:showToast("Chưa có đội bóng yêu thích")
                      }
                      is Resource.Error -> {
                          binding.progressBar.visibility = View.GONE
                          binding.swipeRefreshLayout.isRefreshing = false
                          teams.message?.let { showToast(it) }
                      }
                      is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                  }
              }
          }
        }
    }

    private fun showToast(it: String) {
        Toast.makeText(this, it, Toast.LENGTH_LONG, ).show()
    }
    private fun navigateToTeamMatches(teamId: String) {
        val  teamMatchesFragment = TeamMatchesFragment.newInstance(teamId)
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,  // Animation khi fragment cũ ra
            )
            .replace(R.id.frame,teamMatchesFragment)
            .addToBackStack(null)
            .commit()
    }
}