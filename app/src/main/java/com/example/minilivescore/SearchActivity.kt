package com.example.minilivescore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.room.util.query
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.minilivescore.data.database.AppDatabase
import com.example.minilivescore.domain.repository.SearchRepository
import com.example.minilivescore.domain.repository.TeamViewModelFactory
import com.example.minilivescore.databinding.ActivitySearchBinding
import com.example.minilivescore.presentation.ui.searchteam.SearchTeamViewModel
import com.example.minilivescore.presentation.ui.searchteam.SearchTeamsFragment
import com.example.minilivescore.utils.LiveScoreMiniServiceLocator
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE){ActivitySearchBinding.inflate(layoutInflater)}


    @Inject lateinit var database: AppDatabase
    private val teamDao by lazy {
        database.LiveScoreDao()
    }
    val repository by lazy {
        SearchRepository(
            liveScoreApiService = LiveScoreMiniServiceLocator.liveScoreApiService,
            livescoreDao = teamDao
        )
    }
/*
    private val viewModel: SearchTeamViewModel by viewModels {
        TeamViewModelFactory(repository)
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupViewPager()
        setupToolbar()
        setUpSearchInput()
        lifecycleScope.launch {
            val users = teamDao.getAllTeams() // Thực hiện truy vấn
            // Xử lý dữ liệu và cập nhật UI
            users.forEach { user ->
                Log.d("UserListActivity", "User: ${user.name}, Age: ${user.shortName}")
            }
        }
    }
    private fun setUpSearchInput() {
        binding.searchInput.addTextChangedListener(object :TextWatcher{
            private var searchJob :Job?= null
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                searchJob?.cancel()

                searchJob = lifecycleScope.launch {
                    delay(300)
                    p0?.toString()?.let { query ->
                        if(query.length >= 2){
                            //lấy fragment hiện tại
                            val currentFragment = supportFragmentManager.findFragmentByTag("f${binding.searchViewPager.currentItem}")
                            if (currentFragment is SearchTeamsFragment) {
                                currentFragment.viewModel.searchTeam(query)
                            }
                        }
                    }
                }
            }

        })
    }
    private fun setupToolbar() {
        binding.searchToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewPager() {
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.searchViewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.searchTabLayout, binding.searchViewPager) { tab, position ->
            tab.text = when (position) {
                0 ->    "Câu lạc bộ"
                1 ->    "Cầu thủ"
                else -> null
            }
        }.attach()

    }

    private inner class ScreenSlidePagerAdapter(fragmentActivity: SearchActivity):FragmentStateAdapter(fragmentActivity){
        override fun getItemCount(): Int {
           return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> SearchTeamsFragment.newInstance()
                1 -> SearchTeamsFragment.newInstance()
                else -> throw IllegalArgumentException("Invalid position $position")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}