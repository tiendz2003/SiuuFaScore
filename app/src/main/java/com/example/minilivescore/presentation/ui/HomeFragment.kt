package com.example.minilivescore.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.minilivescore.databinding.FragmentHomeBinding
import com.example.minilivescore.presentation.ui.matches.MatchesFragment
import com.example.minilivescore.presentation.ui.matches.MatchesViewModel
import com.example.minilivescore.presentation.ui.standing.StandingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MatchesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setDefaultLeague()
        }
       // viewModel.setCurrentLeague("PL")
       // setDefaultLeague()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

    }
    private fun setDefaultLeague(){
     /*     lifecycleScope.launch {
              viewModel.currentMatchday
                  .collect { matchday ->
                  Log.d("MainActivity", "Vòng đấu hiện tại: $matchday")
                  viewModel.fetchMatches("PL",matchday)
              }

          }*/
        lifecycleScope.launch {
            val matchday = viewModel.currentMatchday.filterNotNull().distinctUntilChanged().first()
            Log.d("MainActivity", "Vòng đấu hiện tại: $matchday")
            viewModel.fetchMatches("PL", matchday)
        }
        viewModel.getStandingLeagues("PL")
    }
    private fun setupViewPager() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewpagerMain.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewpagerMain) { tab, position ->
            tab.text = when (position) {
                0 -> "Lịch thi đấu"
                1 -> "Bảng xếp hạng"
                else -> null
            }
        }.attach()
    }



    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MatchesFragment.newInstance()
                1 -> StandingFragment.newInstance()
                else -> throw IllegalArgumentException("Invalid position $position")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}