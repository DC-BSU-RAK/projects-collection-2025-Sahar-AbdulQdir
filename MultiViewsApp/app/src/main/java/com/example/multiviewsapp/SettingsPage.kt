package com.example.multiviewsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yariksoffice.lingver.Lingver

class SettingsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Language Buttons
        val englishBtn = findViewById<Button>(R.id.btnEnglish)
        val arabicBtn = findViewById<Button>(R.id.btnArabic)
        val urduBtn = findViewById<Button>(R.id.btnUrdu)
        val backB = findViewById<ImageButton>(R.id.backBtn)

        backB.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        englishBtn.setOnClickListener {
            Lingver.getInstance().setLocale(this, "en")
            recreate()
        }

        arabicBtn.setOnClickListener {
            Lingver.getInstance().setLocale(this, "ar")
            recreate()
        }

        urduBtn.setOnClickListener {
            Lingver.getInstance().setLocale(this, "ur")
            recreate()
        }

        // Toast & Sound Switches
        val toastSwitch = findViewById<Switch>(R.id.switchMuteToast)
        val soundSwitch = findViewById<Switch>(R.id.switchMuteSound)

        // Set initial states
        toastSwitch.isChecked = AppSettingsManager.isToastMuted(this)
        soundSwitch.isChecked = AppSettingsManager.isSoundMuted(this)

        // Handle switch toggles
        toastSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppSettingsManager.setToastMuted(this, isChecked)
            SingleToast.show(this, getString(R.string.toast_muted), Toast.LENGTH_SHORT)
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppSettingsManager.setSoundMuted(this, isChecked)
            SingleToast.show(this, if (isChecked) "Sounds muted" else "Sounds unmuted", Toast.LENGTH_SHORT)
        }
    }

    //    language
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

}
