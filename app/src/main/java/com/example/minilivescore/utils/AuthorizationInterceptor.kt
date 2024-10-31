package com.example.minilivescore.utils

import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(
    private val clientId:String
) :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.request()
            .newBuilder()
            .addHeader("X-Auth-Token",clientId)
            .build()
            .let (chain::proceed )
}
