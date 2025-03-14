package com.example.minilivescore.utils

import com.example.minilivescore.data.di.YoutubeClientId
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class YouTubeAuthorizationInterceptor @Inject constructor(
    @YoutubeClientId
    private val apiKey:String
):Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val url = originRequest.url.newBuilder()
            .addQueryParameter("key",apiKey)
            .build()
        val newRequest = originRequest.newBuilder()
            .url(url)
            .build()
        return chain.proceed(newRequest)
    }
}