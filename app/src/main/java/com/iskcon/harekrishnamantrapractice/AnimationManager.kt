package com.iskcon.harekrishnamantrapractice

import android.os.Handler
import android.os.Looper
import android.widget.TextView

class AnimationManager(private val tVs: Array<TextView>) {

    @Volatile
    private var switchon: Boolean = false
    private var animationThread: Thread? = null

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
                                }, 400)
                            }, 400)
                            return
                        }

                        handler.post {
                            tVs[index].scaleX = 1.5f
                            tVs[index].scaleY = 1.5f
                        }

                        handler.postDelayed({
                            scaleUpOneByOne(index + 1)
                        }, 400)
                    }

                    try {
                        while (!Thread.currentThread().isInterrupted) {
                            if (switchon) {
                                handler.post {
                                    scaleUpOneByOne(0)
                                }
                                Thread.sleep((400 * (tVs.size + 1) + 400).toLong())
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
}