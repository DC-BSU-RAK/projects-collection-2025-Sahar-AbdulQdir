package com.example.multiviewsapp

import android.app.Application
import com.yariksoffice.lingver.Lingver

class LanguageClass : Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this, "en") // default language
    }
}
