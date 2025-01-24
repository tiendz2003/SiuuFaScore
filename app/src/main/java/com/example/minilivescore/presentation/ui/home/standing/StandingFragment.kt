package com.example.minilivescore.presentation.ui.home.standing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minilivescore.MainActivity
import com.example.minilivescore.R
import com.example.minilivescore.databinding.FragmentStandingBinding
import com.example.minilivescore.presentation.base.BaseFragment
import com.example.minilivescore.presentation.ui.home.MatchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StandingFragment : BaseFragment<FragmentStandingBinding>(FragmentStandingBinding::inflate) {

    private val viewModel by  activityViewModels<MatchesViewModel>()
    private val adapter: LeaguesStandingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        LeaguesStandingAdapter(Glide.with(this))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        observeViewModel()
        observeCurrentLeague()
        binding.swipeRefreshLayout.setOnRefreshListener {
            (activity as? MainActivity)?.let {
                when(it.binding.navViewMain.selectedItemId){
                    R.id.navigation_matches_football -> viewModel.getStandingLeagues("PL")
                    R.id.navigation_matches_basketball -> viewModel.getStandingLeagues("SA")
                    R.id.navigation_matches_american_football -> viewModel.getStandingLeagues("DA")
                }
            }
        }
    }
    private fun observeViewModel(){
        //note:Nhớ từng trường hợp của dữ liệu như loading, success, error, thiết lập với StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.standingLeague.collect { standing ->
                val tableList = standing.data?.standings?.flatMap { it.table }
                binding.swipeRefreshLayout.isRefreshing = false
                if(standing.data?.standings?.isNotEmpty() == true){
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noEventsTextView.visibility =View.GONE
                    adapter.submitList(tableList)
                }else{
                    binding.recyclerView.visibility = View.GONE
                    binding.noEventsTextView.visibility = View.VISIBLE
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.recyclerView.visibility = View.GONE
            binding.noEventsTextView.visibility = View.VISIBLE
            binding.noEventsTextView.text = errorMessage
        }

    }
    private fun observeCurrentLeague() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentLeague.collect { leagueCode ->
                    val (leagueName,leagueLogo) = when(leagueCode) {
                        "PL" -> Pair("Premier League",R.drawable.premier_league_idhcr6mt55_6)
                        "SA" -> Pair("Serie A",R.drawable.seria)
                        "PD" -> Pair("La Liga",R.drawable.laliga)
                        else -> Pair("Không có giải đấu nào",R.drawable.images)
                    }
                    binding.standingHeader.textView.text = leagueName
                    Glide.with(requireContext())
                        .load(leagueLogo)
                        .fitCenter()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.standingHeader.imageView)
                }
            }
        }
    }
    private fun setupRecycleView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }


    companion object {

        @JvmStatic
        fun newInstance() = StandingFragment()
    }
}