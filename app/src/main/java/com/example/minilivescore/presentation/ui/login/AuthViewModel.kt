package com.example.minilivescore.presentation.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minilivescore.LoginActivity
import com.example.minilivescore.MainActivity
import com.example.minilivescore.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
):ViewModel() {
    private var _loginState= MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.login(email,password).catch {error ->
                _loginState.value = LoginState.Error(error.message ?: "Đã xảy ra lỗi")
            }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _loginState.value = LoginState.Success(it)
                        },
                        onFailure = {
                            _loginState.value= LoginState.Error(it.message ?: "Đã xảy ra lỗi")
                        }
                    )
                }
             }
    }

    fun loginWithGoogle(activity:Activity){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.loginWithGoogle(activity).catch {error ->
                _loginState.value = LoginState.Error(error.message ?: "Đăng nhập thất bại")

            }
                .collect{result ->
                    result.fold(
                        onSuccess = {user->
                            _loginState.value = LoginState.Success(user)
                        },
                        onFailure = {
                            _loginState.value= LoginState.Error(it.message ?: "Đã xảy ra lỗi")
                        }
                    )

                }
        }
    }
    fun isUserLoggedIn(): Boolean {
        // Kiểm tra trực tiếp từ AuthRepository mà không dùng getCurrentUser()
        return authRepository.getCurrentUser() != null
    }
    fun navigateBasedOnAuthStatus(context:Context){
        val intent =if(isUserLoggedIn()){
            Intent(context, MainActivity::class.java)
        }else{
            Intent(context, LoginActivity::class.java)
        }
        context.startActivity(intent)
    }

}