    package com.example.minilivescore.presentation.ui.detailmatch

    import android.net.Uri
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
    import com.amazonaws.ivs.player.Player
    import com.amazonaws.ivs.player.PlayerException
    import com.bumptech.glide.Glide
    import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
    import com.example.minilivescore.R
    import com.example.minilivescore.data.model.football.LeagueMatches
    import com.example.minilivescore.domain.repository.HighlightRepository
    import com.example.minilivescore.domain.repository.MatchRepository
    import com.example.minilivescore.databinding.FragmentDetailMatchBinding
    import com.example.minilivescore.extension.loadImage
    import com.example.minilivescore.utils.LiveScoreMiniServiceLocator
    import com.example.minilivescore.utils.Preferences
    import com.example.minilivescore.utils.Resource
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.Dispatchers

    @AndroidEntryPoint
    class DetailMatchFragment : Fragment() {
        private var _binding :FragmentDetailMatchBinding? = null
        private val binding get() = _binding!!
        private val args : DetailMatchFragmentArgs by navArgs()
        private lateinit var matches: LeagueMatches.Matche
        private var player: Player?= null

        private val viewModel by viewModels<MatchDetailViewModel>()
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
            updateUI()
           // val playbackUrl = " https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"
            setupPLayer()
            lifecycle.addObserver(binding.youtubePlayerView)
            observeViewModel()
            val homeTeam = matches.homeTeam.shortName
            val awayTeam = matches.awayTeam.shortName
            val title ="${homeTeam.uppercase()} - ${awayTeam.uppercase()}"
            viewModel.loadHighlight(title)
        }


        private fun updateUI(){
            matches =args.match

            observeStreamUrl(matches.id)
            Log.d("Match","${matches.id}")
            binding.homeTeamName.text = matches.homeTeam.tla
            binding.awayTeamName.text = matches.awayTeam.tla
            binding.tvDateTime.text = Preferences.formatDate(matches.utcDate)
            binding.matchScore.text = "${matches.score.fullTime?.home?:"-"} - ${matches.score.fullTime?.away?:"-"}"
          /*  Glide.with(this)
                .load(matches.homeTeam.crest)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.homeTeamLogo)*/
            binding.homeTeamLogo.loadImage(matches.homeTeam.crest)
            binding.awayTeamLogo.loadImage(matches.awayTeam.crest)
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
        private fun setupPLayer(){
            val playerView = binding.liveScreen
            player = playerView.player
            player?.addListener(object: IvsPLayerState() {

                override fun onStateChanged(state: Player.State) {
                    when(state){
                        Player.State.READY -> player?.play()
                        Player.State.PLAYING -> binding.progressBar.visibility = View.GONE
                        Player.State.BUFFERING -> showLoading()
                        Player.State.IDLE -> showError("Lỗi khi tải dữ liệu")
                        Player.State.ENDED -> "SIYYY"
                    }
                }

                override fun onError(p0: PlayerException) {
                    p0.message?.let { showError(it) }
                }

            })
        }
        private fun showVideo(videoId: String) {
            binding.progressBar.visibility = View.GONE
            binding.youtubePlayerView.visibility = View.VISIBLE
            binding.youtubePlayerView.addYouTubePlayerListener(object :AbstractYouTubePlayerListener(){
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })
        }

        private fun showLoading() {

            binding.progressBar.visibility = View.VISIBLE
        }
        private fun showError(message: String) {
            // Hiển thị thông báo lỗi
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
        private fun observeStreamUrl(matchId: Int) {
            viewModel.getPlayBackUrl(matchId).observe(viewLifecycleOwner) { streamUrl ->
                val url =  streamUrl.data?.playBackUrl
                Log.d("StreamUrl","$url")
                if(!url.isNullOrEmpty()){
                    try {
                        player?.load(Uri.parse(url))
                    }catch (e:Exception){
                        showError("Loading stream URL failed: ${e.message}")
                    }
                }
            }
        }

        override fun onDestroyView() {
            _binding = null
            super.onDestroyView()

            //loại bỏ tham chiếu khi fragmemt bị hủy
            val youtubePlayerView: YouTubePlayerView? = view?.findViewById(R.id.youtube_player_view)
            youtubePlayerView?.release()
            player?.release()
            player = null
        }
        companion object {

            @JvmStatic
            fun newInstance() = DetailMatchFragment()
        }
    }