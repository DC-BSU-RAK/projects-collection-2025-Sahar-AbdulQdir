package com.example.multiviewsapp

import android.content.Context
import android.widget.Toast

object SingleToast {
    private var currentToast: Toast? = null
    private var currentMessage: String? = null

    fun show(context: Context, message: String, duration: Int) {
        if (AppSettingsManager.isToastMuted(context)) return

        if (message == currentMessage && currentToast != null) {
            currentToast?.cancel()
        }

        currentToast = Toast.makeText(context, message, duration)
        currentToast?.show()
        currentMessage = message
    }
}
