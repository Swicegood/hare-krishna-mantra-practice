package com.iskcon.harekrishnamantrapractice

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.util.Log

class AnimationManager(private val tVs: Array<TextView>) {

    @Volatile
    private var switchon: Boolean = false
    @Volatile
    private var animationSpeed: Int = 1000 // Default speed in milliseconds
    private val handler = Handler(Looper.getMainLooper())

    fun startAnimation() {
        synchronized(this) {
            if (!switchon) {
                switchon = true
                scaleUpOneByOne(0)
            }
        }
    }

    private fun scaleUpOneByOne(index: Int) {
        if (!switchon) return
        
        if (index >= tVs.size) {
            handler.postDelayed({
                tVs.forEach { tv ->
                    tv.scaleX = 1f
                    tv.scaleY = 1f
                }
                handler.postDelayed({
                    if (switchon) {
                        scaleUpOneByOne(0)
                    }
                }, animationSpeed.toLong())
            }, animationSpeed.toLong())
            return
        }

        handler.post {
            tVs[index].scaleX = 1.5f
            tVs[index].scaleY = 1.5f
        }

        handler.postDelayed({
            scaleUpOneByOne(index + 1)
        }, animationSpeed.toLong())
    }

    fun stopAnimation() {
        synchronized(this) {
            switchon = false
            handler.removeCallbacksAndMessages(null) // Remove all pending callbacks
        }
    }

    fun updateAnimationSpeed(speed: Int) {
        animationSpeed = speed
        Log.d("AnimationManager", "Updated animation speed to $speed milliseconds")
        // No need to restart - the new speed will be picked up on the next animation cycle
    }
}