package com.example.minilivescore

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.minilivescore.databinding.ActivityLoginBinding
import com.example.minilivescore.presentation.ui.login.AuthViewModel
import com.example.minilivescore.presentation.ui.login.LoginState
import com.example.minilivescore.utils.Preferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val viewModel:AuthViewModel by viewModels<AuthViewModel>()
    private val binding:ActivityLoginBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
       // checkLogin()
        observeViewModel()
        setupView()

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect{state ->
                when(state){
                    is LoginState.Loading -> binding.progressBarLogin.visibility = View.VISIBLE
                    is LoginState.Success -> {
                        binding.progressBarLogin.visibility = View.GONE
                        navigateToMainScreen()
                    }
                    is LoginState.Error -> {
                        binding.progressBarLogin.visibility = View.GONE
                       Preferences.showAlert(this@LoginActivity,state.message)
                    }

                    LoginState.Initial -> Unit
                }
            }

        }
    }
    private fun setupView() {

        binding.loginButton.setOnClickListener {
            val email = binding.usernameEditText.text.toString().trim()
            val pass = binding.passwordEditText.text.toString().trim()
            viewModel.login(email, pass)
        }
        binding.googleButton.setOnClickListener {
            viewModel.loginWithGoogle(this)
        }
    }


    private fun navigateToMainScreen() {
        // Chuyển đến màn hình chính
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}