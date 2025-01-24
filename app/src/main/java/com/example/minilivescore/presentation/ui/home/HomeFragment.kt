package com.example.minilivescore.presentation.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.example.minilivescore.databinding.FragmentHomeBinding
import com.example.minilivescore.presentation.base.BaseFragment
import com.example.minilivescore.presentation.ui.home.matches.MatchesFragment
import com.example.minilivescore.presentation.ui.home.standing.StandingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel by activityViewModels<MatchesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setDefaultLeague()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()

    }
    private fun setDefaultLeague(){

        lifecycleScope.launch {
           val matchDay =viewModel.currentMatchday.filterNotNull().distinctUntilChanged().first()
            viewModel.fetchMatches("PL",matchDay)
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

    companion object {
        fun newInstance() = HomeFragment()
    }
}