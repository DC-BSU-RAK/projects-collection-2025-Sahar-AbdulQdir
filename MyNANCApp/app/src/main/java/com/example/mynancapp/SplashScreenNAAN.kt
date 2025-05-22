package com.example.mynancapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreenNAAN : AppCompatActivity() {
        private val SPLASH_TIME_OUT: Long = 2000 // Splash screen timeout in milliseconds
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_splash_screen_naan)

            val videoView = findViewById<VideoView>(R.id.splashVideo)
            val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.spalshvid}")
            videoView.setVideoURI(videoUri)

            videoView.setOnCompletionListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            videoView.start()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            // Wait for 5 seconds, then go to MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Finish SplashActivity so user can’t return to it
            }, 3500)
        }
    }

