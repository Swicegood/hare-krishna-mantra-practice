package com.example.harekrishnamantrapractice

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.OneTimeWorkRequest
import com.example.harekrishnamantrapractice.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val workRequest = OneTimeWorkRequest.Builder(MantraCounter::class.java).build()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener {
            var count = 0

            // Create a new SpeechRecognizer object
            val recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            // Request permission to use the microphone
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            }
            // Create a new Intent to recognize speech
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            // Set the mantra that we want to recognize
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "om")

            val listener = object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // Do something when the speech input is ready
                }

                override fun onBeginningOfSpeech() {
                    // Do something when the speech input has started
                }

                override fun onRmsChanged(p0: Float) {
                    TODO("Not yet implemented")
                }

                override fun onBufferReceived(p0: ByteArray?) {
                    TODO("Not yet implemented")
                }

                override fun onEndOfSpeech() {
                    TODO("Not yet implemented")
                }

                override fun onError(p0: Int) {
                    TODO("Not yet implemented")
                }

                override fun onResults(p0: Bundle?) {
                    TODO("Not yet implemented")
                }

                override fun onPartialResults(p0: Bundle?) {
                    println("We are listening. We have received results!")
                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                    TODO("Not yet implemented")
                }

                // Other overridden methods go here
            }

            // Start the recognition process
            recognizer.setRecognitionListener(listener)
            recognizer.startListening(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
// Create a new Worker class to run the recognition code
class MantraCounter(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    // Override the doWork() method
    override fun doWork(): Result {
        // Create a global variable to hold the count of mantras

        // Return Result.success() to indicate that the task was completed successfully
        return Result.success()
    }
}
