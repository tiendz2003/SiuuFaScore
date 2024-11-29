package com.example.minilivescore

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.minilivescore.domain.repository.MatchRepository
import com.example.minilivescore.databinding.SharedActivityLayoutBinding
import com.example.minilivescore.presentation.ui.matches.MatchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
     lateinit var binding: SharedActivityLayoutBinding
    private val viewModel by viewModels<MatchesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //phải gọi trước setContentView
        installSplashScreen()
        enableEdgeToEdge()
        binding = SharedActivityLayoutBinding.inflate(layoutInflater)
        // Kiểm soát thời gian hiển thị của splash screen
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain.toolbar)
        setupBottomNavigation()
        setOnClickListener()
    }


    private fun setupBottomNavigation() {
        binding.navViewMain.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_matches_football -> {
                    fetchCurrentLeagueUI("PL")
                    true
                }
                R.id.navigation_matches_basketball -> {
                    fetchCurrentLeagueUI("SA")
                    true
                }
                R.id.navigation_matches_american_football -> {
                    fetchCurrentLeagueUI("PD")
                    true
                }
                else -> false
            }
        }
    }
    //Cập nhật UI cho từng giải đấu
    private fun fetchCurrentLeagueUI(leagueCode: String) {
        viewModel.apply {
            setCurrentLeague(leagueCode)
            getStandingLeagues(leagueCode)
        }
    }
    private fun setOnClickListener() {
        binding.toolbarMain.searchIcon.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
}