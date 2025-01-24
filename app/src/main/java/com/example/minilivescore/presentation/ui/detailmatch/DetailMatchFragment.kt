    package com.example.minilivescore.presentation.ui.detailmatch

    import android.graphics.Color
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import androidx.appcompat.widget.Toolbar
    import androidx.fragment.app.viewModels
    import androidx.lifecycle.lifecycleScope
    import androidx.navigation.fragment.navArgs
    import com.amazonaws.ivs.player.Player
    import com.amazonaws.ivs.player.PlayerException
    import com.example.minilivescore.R
    import com.example.minilivescore.data.model.football.FavoriteTeam
    import com.example.minilivescore.data.model.football.LeagueMatches
    import com.example.minilivescore.extension.loadImage
    import com.example.minilivescore.presentation.base.BaseFragment
    import com.example.minilivescore.databinding.FragmentDetailMatchBinding
    import com.example.minilivescore.presentation.ui.detailmatch.comment.BottomSheetFragment
    import com.example.minilivescore.presentation.ui.detailmatch.comment.CommentAdapter
    import com.example.minilivescore.utils.Preferences
    import com.example.minilivescore.utils.Resource
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import com.google.android.material.transition.MaterialContainerTransform
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
    import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.launch


    @AndroidEntryPoint
    class DetailMatchFragment : BaseFragment<FragmentDetailMatchBinding>(FragmentDetailMatchBinding::inflate) {
        private val args : DetailMatchFragmentArgs by navArgs()
        private lateinit var matches: LeagueMatches.Matche
        private var player: Player?= null
        private lateinit var commentAdapter: CommentAdapter
        private lateinit var favTeam: FavoriteTeam
        private lateinit var favTeam1: FavoriteTeam
        private val viewModel by viewModels<MatchDetailViewModel>()
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            sharedElementEnterTransition = MaterialContainerTransform().apply {
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
                scrimColor = Color.TRANSPARENT
            }
            sharedElementReturnTransition = MaterialContainerTransform().apply {
                duration = 300
                fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
                scrimColor = Color.TRANSPARENT
            }
        }
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
            setupPLayer()
            getFavButtonState()

            lifecycle.addObserver(binding.youtubePlayerView)
            observeViewModel()
            val homeTeam = matches.homeTeam.shortName
            val awayTeam = matches.awayTeam.shortName
            val title ="${homeTeam.uppercase()} - ${awayTeam.uppercase()}"
            viewModel.loadHighlight(title)

        }

        private fun getFavButtonState() {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getFav(favTeam.id).collect { isFavorite ->
                    binding.btnFav1.isSelected = isFavorite
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getFav(favTeam1.id).collect { isFavorite ->
                    binding.btnFav2.isSelected = isFavorite
                }
            }
        }


        private fun updateUI(){

            matches =args.match
             favTeam = FavoriteTeam(matches.homeTeam.id.toString(),matches.homeTeam.shortName,matches.homeTeam.crest)
             favTeam1 = FavoriteTeam(matches.awayTeam.id.toString(),matches.awayTeam.shortName,matches.awayTeam.crest)
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
            //tạo 1 lambda tránh mã lặp
            val favClicklistener:(View,FavoriteTeam) ->Unit = {button,favTeam ->
                val newState = handleFavoriteButton(button,favTeam,viewModel::toggleFavTeam)
                viewModel.saveFavoriteState(favTeam.id,newState)
            }
            binding.apply {
                btnFav1.setOnClickListener { favClicklistener(btnFav1,favTeam) }
                btnFav2.setOnClickListener { favClicklistener(btnFav2,favTeam1) }
                /*
                btnFav1.setOnClickListener {
                    viewModel.saveFavoriteState(
                        favTeam.id,
                        !btnFav1.isSelected
                    )
                handleFavoriteButton(
                      btnFav1,
                      favTeam,
                      viewModel::toggleFavTeam,
                     // viewModel::deleteFavoriteTeam
                  )
                }
                btnFav2.setOnClickListener {
                    viewModel.saveFavoriteState(
                        favTeam1.id,
                        !btnFav2.isSelected
                    )
                   handleFavoriteButton(
                        btnFav2,
                        favTeam1,
                       viewModel::toggleFavTeam,
                       // viewModel::deleteFavoriteTeam
                    )
                }*/
            }
            binding.homeTeamLogo.loadImage(matches.homeTeam.crest)
            binding.awayTeamLogo.loadImage(matches.awayTeam.crest)
            binding.reply.setOnClickListener {
                navigationToBottomSheet(matches.id.toString())
            }
            binding.logoLeague.loadImage(matches.competition.emblem)
            binding.matchScore.transitionName = "score_${matches.id}"
            Preferences.setFontStyle(binding.matchScore)
            Preferences.setFontStyle(binding.homeTeamName)
            Preferences.setFontStyle(binding.awayTeamName)
            //setBackground cho từng trận
            Preferences.applyBackground(matches.homeTeam.tla,binding.homeTeamScore)
            Preferences.applyBackground(matches.awayTeam.tla,binding.awayTeamScore)
            Preferences.updateTextColorBasedOnGradient(binding.awayTeamScore,binding.awayTeamName)
            Preferences.updateTextColorBasedOnGradient(binding.homeTeamScore,binding.homeTeamName)

        }

        //Great code should be learn it
        private fun handleFavoriteButton(
            button: View,
            team: FavoriteTeam,
            onToggleFav: (FavoriteTeam,Boolean) -> Unit,
           // onDelete: (String) -> Unit
        ):Boolean{

            //Đảo ngược lại trang thái của nút
            val newState =  !button.isSelected
            //Cập nhật UI
            button.isSelected = newState
            //Xử lý logic thêm xóa
            if (newState) {
                Toast.makeText(button.context, "Đã thêm ${team.name} vào đội yêu thích", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(button.context, "Đã xóa ${team.name} từ đội yêu thích", Toast.LENGTH_SHORT).show()
            }
            onToggleFav(team,newState)
            return newState
        }

        private fun navigationToBottomSheet(matchId: String){
            val bottomSheetFragment = BottomSheetFragment()
            val bundle = Bundle().apply {
                putString("matchId",matchId)
            }
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(childFragmentManager,bottomSheetFragment.tag)
        }
        private fun observeViewModel() {
            viewModel.highlightState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> state.data?.let { showVideo(it) }
                    is Resource.Error -> Log.d("detail", "${state.message}")
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