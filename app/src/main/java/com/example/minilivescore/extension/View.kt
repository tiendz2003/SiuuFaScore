package com.example.minilivescore.extension

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun View.setOnDebounceClickListener(interval:Long = 500L, onClick:(View)->Unit){
    var job:Job? = null
    this.setOnClickListener {
        job?.cancel()
        job = GlobalScope.launch (Dispatchers.Main){
            delay(interval)
            onClick(it)
        }
    }
}
fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .fitCenter()
        .transition(DrawableTransitionOptions().crossFade())
        .into(this)
}