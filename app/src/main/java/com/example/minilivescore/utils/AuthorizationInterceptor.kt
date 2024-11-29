package com.example.minilivescore.utils

import com.example.minilivescore.data.di.FootBallClientId
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor@Inject constructor(
    @FootBallClientId
    private val clientId:String
) :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.request()
            .newBuilder()
            .addHeader("X-Auth-Token",clientId)
            .build()
            .let (chain::proceed )
}
