package com.example.multiviewsapp

import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.content.Context
import android.view.ViewGroup



object ButtonAnimator {

    fun applyAllAnimations(context: Context, button: Button) {
        val scaleAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_button)

        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.alpha = .9f
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> view.alpha = .9f
            }
            false
        }

        button.setOnClickListener {
            it.startAnimation(scaleAnimation)
            // Add more behavior here if needed
        }
    }

    fun applyToAllButtons(root: View, context: Context) {
        if (root is Button) {
            applyAllAnimations(context, root)
        } else if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                applyToAllButtons(root.getChildAt(i), context)
            }
        }
    }
}
