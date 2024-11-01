package com.example.minilivescore.ui.detailmatch

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.R
import com.example.minilivescore.data.model.LeagueMatches
import com.example.minilivescore.data.repository.HighlightRepository
import com.example.minilivescore.databinding.FragmentDetailMatchBinding
import com.example.minilivescore.utils.Preferences
import com.example.minilivescore.utils.Resource
import com.example.minilivescore.utils.YouTubeServiceLocator
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.Date


class DetailMatchFragment : Fragment() {
    private var _binding :FragmentDetailMatchBinding? = null
    private val binding get() = _binding!!
    private val args : DetailMatchFragmentArgs by navArgs()
    private lateinit var matches: LeagueMatches.Matche

    private val viewModel by viewModels<MatchDetailViewModel>(
        factoryProducer = {
            viewModelFactory {
                addInitializer(MatchDetailViewModel::class){
                   MatchDetailViewModel(
                       highlightRepository = HighlightRepository(
                           youTubeApiService = YouTubeServiceLocator.youtubeApiService
                       )
                   )
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailMatchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.addObserver(binding.youtubePlayerView)
        observeViewModel()

        updateUI()
        val homeTeam = matches.homeTeam.name
        val awayTeam = matches.awayTeam.name
        val matchEndTime = Date()
        viewModel.loadHighlight(homeTeam, awayTeam, matchEndTime)
    }
    private fun updateUI(){
        matches =args.match

        Log.d("Match","$matches")
        binding.homeTeamName.text = matches.homeTeam.tla
        binding.awayTeamName.text = matches.awayTeam.tla
        binding.tvDateTime.text = Preferences.formatDate(matches.utcDate)
        binding.matchScore.text = "${matches.score.fullTime?.home?:"-"} - ${matches.score.fullTime?.away?:"-"}"
        Glide.with(this)
            .load(matches.homeTeam.crest)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.homeTeamLogo)
        Glide.with(this)
            .load(matches.awayTeam.crest)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.awayTeamLogo)
        Preferences.setFontStyle(binding.matchScore)
        Preferences.setFontStyle(binding.homeTeamName)
        Preferences.setFontStyle(binding.awayTeamName)
        //setBackground cho từng trận
        Preferences.applyBackground(matches.homeTeam.tla,binding.homeTeamScore)
        Preferences.applyBackground(matches.awayTeam.tla,binding.awayTeamScore)
        Preferences.updateTextColorBasedOnGradient(binding.awayTeamScore,binding.awayTeamName)
        Preferences.updateTextColorBasedOnGradient(binding.homeTeamScore,binding.homeTeamName)

    }
    private fun observeViewModel(){
        viewModel.highlightState.observe(viewLifecycleOwner){state ->
            when (state) {
                is Resource.Loading -> showLoading()
                is Resource.Success -> state.data?.let { showVideo(it) }
                is Resource.Error -> Log.d("detail","${state.message}")
            }

        }
    }

    private fun showVideo(videoId: String) {
        binding.progressBar.visibility = View.GONE
        binding.youtubePlayerView.visibility = View.VISIBLE
        binding.youtubePlayerView.addYouTubePlayerListener(object :AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    private fun showLoading() {

        binding.progressBar.visibility = View.GONE
    }
    private fun showError(message: String) {
        // Hiển thị thông báo lỗi
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()

        //loại bỏ tham chiếu khi fragmemt bị hủy
        val youtubePlayerView: YouTubePlayerView? = view?.findViewById(R.id.youtube_player_view)
        youtubePlayerView?.release()
    }
    companion object {

        @JvmStatic
        fun newInstance() = DetailMatchFragment()
    }
}