    package com.example.minilivescore.presentation.ui.detailmatch

    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.appcompat.widget.Toolbar
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.findNavController
    import androidx.navigation.fragment.navArgs
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.amazonaws.ivs.player.Player
    import com.amazonaws.ivs.player.PlayerException
    import com.example.minilivescore.MainActivity
    import com.example.minilivescore.R
    import com.example.minilivescore.data.model.football.FavoriteTeam
    import com.example.minilivescore.data.model.football.LeagueMatches
    import com.example.minilivescore.databinding.FragmentDetailMatchBinding
    import com.example.minilivescore.domain.repository.FavoriteTeamRepository
    import com.example.minilivescore.extension.loadImage
    import com.example.minilivescore.presentation.base.BaseFragment
    import com.example.minilivescore.presentation.ui.detailmatch.comment.BottomSheetFragment
    import com.example.minilivescore.presentation.ui.detailmatch.comment.CommentAdapter
    import com.example.minilivescore.presentation.ui.searchteam.SearchTeamsFragmentDirections
    import com.example.minilivescore.utils.Preferences
    import com.example.minilivescore.utils.Resource
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.flow.collectLatest
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    @AndroidEntryPoint
    class DetailMatchFragment : BaseFragment<FragmentDetailMatchBinding>(FragmentDetailMatchBinding::inflate) {
        private val args : DetailMatchFragmentArgs by navArgs()
        private lateinit var matches: LeagueMatches.Matche
        private var player: Player?= null
        private lateinit var commentAdapter: CommentAdapter
        @Inject lateinit var favRepository: FavoriteTeamRepository
        private val viewModel by viewModels<MatchDetailViewModel>()

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            commentAdapter = CommentAdapter()
            return super.onCreateView(inflater, container, savedInstanceState)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setupNavigation(false)
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
            val favTeam = FavoriteTeam(matches.homeTeam.id.toString(),matches.homeTeam.shortName,matches.homeTeam.crest)
            val time = Preferences.setupTime(matches.utcDate)
            observeStreamUrl(matches.id)
            viewModel.getComments(matches.id.toString())
            Log.d("Match","${matches.id}")
            binding.homeTeamName.text = matches.homeTeam.tla
            binding.awayTeamName.text = matches.awayTeam.tla
            binding.tvDateTime.text = Preferences.formatDate(matches.utcDate)
            binding.matchScore.text = when{
                matches.score.fullTime?.home == null -> time
                else -> "${matches.score.fullTime?.home} - ${matches.score.fullTime?.away}"
            }
            binding.homeTeamLogo.setOnClickListener {
                viewModel.addFavoriteTeam(favTeam)
                Toast.makeText(context, "Đã thêm ${matches.homeTeam.shortName} vào đội yêu thích", Toast.LENGTH_SHORT).show()
            }
            binding.homeTeamLogo.loadImage(matches.homeTeam.crest)
            binding.awayTeamLogo.loadImage(matches.awayTeam.crest)
            binding.reply.setOnClickListener {
                navigationToBottomSheet(matches.id.toString())
            }
            binding.logoLeague.loadImage(matches.competition.emblem)
            Preferences.setFontStyle(binding.matchScore)
            Preferences.setFontStyle(binding.homeTeamName)
            Preferences.setFontStyle(binding.awayTeamName)
            //setBackground cho từng trận
            Preferences.applyBackground(matches.homeTeam.tla,binding.homeTeamScore)
            Preferences.applyBackground(matches.awayTeam.tla,binding.awayTeamScore)
            Preferences.updateTextColorBasedOnGradient(binding.awayTeamScore,binding.awayTeamName)
            Preferences.updateTextColorBasedOnGradient(binding.homeTeamScore,binding.homeTeamName)

        }
        private fun navigationToBottomSheet(matchId: String){
            val bottomSheetFragment = BottomSheetFragment()
            val bundle = Bundle().apply {
                putString("matchId",matchId)
            }
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(childFragmentManager,bottomSheetFragment.tag)
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

                override fun onError(exception: PlayerException) {
                    exception.message?.let { showError(it) }
                }

            })
        }
        private fun showVideo(videoId: String) {
            binding.youtubePlayerView.visibility = View.VISIBLE
            binding.youtubePlayerView.addYouTubePlayerListener(object :AbstractYouTubePlayerListener(){
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0f)
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
        private fun setupNavigation(isVisible:Boolean){
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_main)
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view_main)
            val visibility = if(isVisible) View.VISIBLE else View.GONE
            toolbar?.visibility = visibility
            bottomNav?.visibility = visibility
        }



        override fun onDestroyView() {
            super.onDestroyView()

            //loại bỏ tham chiếu khi fragmemt bị hủy
            val youtubePlayerView: YouTubePlayerView? = view?.findViewById(R.id.youtube_player_view)
            youtubePlayerView?.release()
            player?.release()
            player = null
           setupNavigation(true)
        }
        companion object {

            @JvmStatic
            fun newInstance() = DetailMatchFragment()
        }
    }