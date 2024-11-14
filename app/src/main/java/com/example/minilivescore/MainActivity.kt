package com.example.minilivescore

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.minilivescore.data.repository.MatchRepository
import com.example.minilivescore.data.repository.MatchesViewModelFactory
import com.example.minilivescore.databinding.SharedActivityLayoutBinding
import com.example.minilivescore.ui.matches.MatchesViewModel
import com.example.minilivescore.utils.LiveScoreMiniServiceLocator


class MainActivity : AppCompatActivity() {
     lateinit var binding: SharedActivityLayoutBinding
     val repository by lazy {
         MatchRepository(apiService = LiveScoreMiniServiceLocator.liveScoreApiService)
     }

    private val viewModel: MatchesViewModel by viewModels {
        MatchesViewModelFactory(
            repository,
            this,
            intent.extras
        )
    }

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
                    viewModel.setCurrentLeague("PL")
                    viewModel.getStandingLeagues("PL")
                    true
                }
                R.id.navigation_matches_basketball -> {
                    viewModel.setCurrentLeague("SA")
                    viewModel.getStandingLeagues("SA")
                    true
                }
                R.id.navigation_matches_american_football -> {
                    viewModel.setCurrentLeague("PD")
                    viewModel.getStandingLeagues("PD")
                    true
                }
                else -> false
            }
        }
    }
    private fun setOnClickListener() {
        binding.toolbarMain.searchIcon.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

}