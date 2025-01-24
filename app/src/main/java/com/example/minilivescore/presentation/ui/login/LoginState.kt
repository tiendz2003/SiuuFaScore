package com.example.minilivescore.presentation.ui.login

import com.google.firebase.auth.FirebaseUser

sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data class Success(val user: FirebaseUser) : LoginState()
    data class Error(val message: String) : LoginState()
}