package com.example.mynancapp.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.mynancapp.R

object ButtonSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.button_sound)
        mediaPlayer?.start()
    }
}
