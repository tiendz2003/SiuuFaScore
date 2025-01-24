package com.example.minilivescore.presentation.ui.detailmatch

import com.amazonaws.ivs.player.Cue
import com.amazonaws.ivs.player.Player
import com.amazonaws.ivs.player.PlayerException
import com.amazonaws.ivs.player.Quality

abstract class IvsPLayerState: Player.Listener() {
    override fun onCue(cue: Cue) {

    }

    override fun onDurationChanged(duration: Long) {

    }

    override fun onStateChanged(state: Player.State) {

    }

    override fun onError(exception: PlayerException) {

    }

    override fun onRebuffering() {

    }

    override fun onSeekCompleted(position: Long) {

    }

    override fun onVideoSizeChanged(width: Int, height: Int) {

    }

    override fun onQualityChanged(quality: Quality) {

    }

}