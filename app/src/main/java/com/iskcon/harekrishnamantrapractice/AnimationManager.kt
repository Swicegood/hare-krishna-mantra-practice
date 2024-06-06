package com.iskcon.harekrishnamantrapractice

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.util.Log

class AnimationManager(private val tVs: Array<TextView>) {

    @Volatile
    private var switchon: Boolean = false
    private var animationThread: Thread? = null
    @Volatile
    private var animationSpeed: Int = 1000 // Default speed in milliseconds

    fun startAnimation() {
        synchronized(this) {
            switchon = true
            if (animationThread == null || !animationThread!!.isAlive) {
                animationThread = Thread {
                    val handler = Handler(Looper.getMainLooper())

                    fun scaleUpOneByOne(index: Int) {
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

                    try {
                        while (!Thread.currentThread().isInterrupted) {
                            if (switchon) {
                                handler.post {
                                    scaleUpOneByOne(0)
                                }
                                Thread.sleep((animationSpeed * (tVs.size + 1) + animationSpeed).toLong())
                            } else {
                                Thread.sleep(100)
                            }
                        }
                    } catch (e: InterruptedException) {
                        // Handle thread interruption gracefully
                        Thread.currentThread().interrupt() // Preserve the interrupt status
                    }
                }

                animationThread?.start()
            }
        }
    }

    fun stopAnimation() {
        synchronized(this) {
            switchon = false
            animationThread?.interrupt()
            animationThread = null // Allow the thread to be garbage collected
        }
    }

    fun updateAnimationSpeed(speed: Int) {
        animationSpeed = speed
        Log.d("AnimationManager", "Updated animation speed to $speed milliseconds")
    }
}