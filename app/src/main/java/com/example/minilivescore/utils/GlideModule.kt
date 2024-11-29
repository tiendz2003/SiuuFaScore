package com.example.minilivescore.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.minilivescore.data.di.FootBallOkHttpClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.inject.Singleton

@GlideModule
class GlideModule:AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val entryPoint = EntryPointAccessors.fromApplication(context, GlideModuleEntryPoint::class.java)
        registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(entryPoint.okHttpClient())
        )
    }
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GlideModuleEntryPoint {
       @FootBallOkHttpClient fun okHttpClient(): OkHttpClient
    }
}