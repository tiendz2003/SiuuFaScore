package com.example.minilivescore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.minilivescore.databinding.SharedActivityLayoutBinding
import com.example.minilivescore.data.repository.AuthRepository
import com.example.minilivescore.presentation.ui.home.MatchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
     lateinit var binding: SharedActivityLayoutBinding
    @Inject
    lateinit var authRepository: AuthRepository
    private val viewModel by viewModels<MatchesViewModel>()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGranted:Boolean->
        if(isGranted){
            LiveScoreMiniApplication.scheduleMatchNotificationWorker(this)
        }

    }
    override fun onStart() {
        super.onStart()
        checkLogin()
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //phải gọi trước setContentView
        val splashScreen =installSplashScreen()
        enableEdgeToEdge()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        splashScreen.setKeepOnScreenCondition{
            viewModel.matchday.value == null
        }
        binding = SharedActivityLayoutBinding.inflate(layoutInflater)
        // Kiểm soát thời gian hiển thị của splash screen
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain.toolbar)
        setupBottomNavigation()
        setOnClickListener()
    }
    //Kiem tra dang nhap
    private fun checkLogin(){
        if(authRepository.getCurrentUser() == null){
            navigateToLogin()
        }
    }
    private fun signOut(){
        authRepository.signOut()
        navigateToLogin()
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Đóng MainActivity để không thể quay lại
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
        binding.toolbarMain.settingsIcon.setOnClickListener {
            signOut()
        }
        binding.toolbarMain.favouritesIcon.setOnClickListener {
            startActivity(Intent(this, FavouriteActivity::class.java))
        }
    }
}