package com.example.minilivescore.presentation.ui.detailteam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.SearchActivity
import com.example.minilivescore.data.model.football.TeamWithDetails
import com.example.minilivescore.databinding.FragmentDetailTeamBinding
import com.example.minilivescore.extension.loadImage
import com.example.minilivescore.presentation.base.BaseFragment
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailTeamFragment : BaseFragment<FragmentDetailTeamBinding>(FragmentDetailTeamBinding::inflate) {
     private val args: DetailTeamFragmentArgs by navArgs()
    private val viewModel: DetailTeamViewModel by viewModels()
    private val adapterPlayer by lazy(LazyThreadSafetyMode.NONE) {
                DetailTeamAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OnCreate","Siuu")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getTeamDetails(args.teamId)
        setupRecycleView()
        observeTeamDetail()

    }

    private fun setupRecycleView(){
        binding.playersRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterPlayer
        }
    }
    private fun observeTeamDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.teamDetails.collect { result ->
                Log.d("Detail","$result")
                when (result) {
                    is Resource.Success -> {
                      binding.progressBar.isVisible = false

                        binding.playersRecyclerView.isVisible = true
                        result.data?.let { player ->
                            Log.d("Siuu", "Player in list: ${player.players}")
                            updateUI(player)
                        }
                        Log.d(
                            "Siuu",
                            "Submitting ${result.data?.players?.size} items to adapter"
                        )

                    }

                    is Resource.Error -> {
                        binding.progressBar.isVisible = false
                        // Hiển thị thông báo lỗi
                    }

                    is Resource.Loading ->  binding.progressBar.isVisible = true
                }
            }
        }
    }
    private fun updateUI(details: TeamWithDetails) {
        binding.apply {
            teamName.text = details.team.name
            val a = details.team.name
            Log.d("A", "Team name: $a")

            // Load team crest
            teamCrest.loadImage(details.team.crest)
            // Update coach info
            details.coach?.let { coach ->
                coachName.text = coach.name
                coachNationality.text = coach.nationality
            }

            // Update players list
            adapterPlayer.submitList(details.players)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailTeamFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailTeamFragment()
    }
}