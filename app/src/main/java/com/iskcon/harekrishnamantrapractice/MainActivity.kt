package com.iskcon.harekrishnamantrapractice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.iskcon.harekrishnamantrapractice.databinding.ActivityMainBinding
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    var mantraCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        toolbar = binding.toolbar

        // Set up the toolbar
        setSupportActionBar(toolbar)

        // Set up the drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up the navigation controller
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Initialize text views
        val tvIDs = arrayOf(
            R.id.textview0, R.id.textview1, R.id.textview2, R.id.textview3,
            R.id.textview4, R.id.textview5, R.id.textview6, R.id.textview7,
            R.id.textview8, R.id.textview9, R.id.textview10, R.id.textview11,
            R.id.textview12, R.id.textview13, R.id.textview14, R.id.textview15
        )
        val tVs = tvIDs.map { binding.root.findViewById<TextView>(it) }.toTypedArray()

        var switchon = true

        binding.fab.setOnClickListener {
            if (switchon) {
                val recognizer = SpeechRecognizer.createSpeechRecognizer(this)

                // Request permission to use the microphone
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        0
                    )
                }

                // Create a new Intent to recognize speech
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                }

                val listener = object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        // Do something when the speech input is ready
                    }

                    override fun onBeginningOfSpeech() {
                        // Do something when the speech input has started
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Do something
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {
                        // Do something
                    }

                    override fun onEndOfSpeech() {
                        // Do something
                    }

                    override fun onError(error: Int) {
                        Log.d("SpeechRecognition", "Error: $error")
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && !switchon) {
                            recognizer.startListening(intent)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val resultList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        resultList?.forEach { result ->
                            result.split(" ").forEach { word ->
                                if (word.contains("Krishna", ignoreCase = true) ||
                                    word.contains("hurry", ignoreCase = true) ||
                                    word.contains("Hare", ignoreCase = true) ||
                                    word.contains("hari", ignoreCase = true) ||
                                    word.contains("हरे", ignoreCase = true) ||
                                    word.contains("ram", ignoreCase = true) ||
                                    word.contains("Merry", ignoreCase = true) ||
                                    word.contains("Christmas", ignoreCase = true) ||
                                    word.contains("Christian", ignoreCase = true) ||
                                    word.contains("कृष्ण", ignoreCase = true) ||
                                    word.contains("Snickers", ignoreCase = true) ||
                                    word.contains("hurray", ignoreCase = true) ||
                                    word.contains("today", ignoreCase = true) ||
                                    word.contains("headache", ignoreCase = true) ||
                                    word.contains("राम", ignoreCase = true) ||
                                    word.contains("rama", ignoreCase = true)
                                ) {
                                    tVs[mantraCounter % 16].setTypeface(null, Typeface.BOLD)
                                    mantraCounter++
                                    supportActionBar?.title = "Nam Man Rnds: $mantraCounter ${mantraCounter / 16} ${mantraCounter / 1728}"
                                    tVs[(mantraCounter - 1) % 16].setTypeface(null, Typeface.NORMAL)
                                }
                            }
                        }
                        recognizer.startListening(intent)
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val resultList = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        resultList?.forEach { result ->
                            if (result.contains("Krishna", ignoreCase = true)) {
                                mantraCounter++
                            }
                        }
                        Log.d("SpeechRecognition", "Partial results: $partialResults")
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {
                        // Do something
                    }
                }

                switchon = false

                // Start the recognition process
                recognizer.setRecognitionListener(listener)
                recognizer.startListening(intent)

                // Create a new thread for the animation
                val animationThread = Thread {
                    var index = 0
                    val handler = Handler(Looper.getMainLooper())
                    while (!switchon) {
                        handler.post {
                            tVs[index].animate()
                                .scaleX(1.5f)
                                .scaleY(1.5f)
                                .setDuration(200)
                                .withEndAction {
                                    tVs[index].animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(200)
                                        .start()
                                }
                                .start()
                        }
                        Thread.sleep(500)
                        index = (index + 1) % tVs.size
                    }
                }
                animationThread.start()
            } else {
                switchon = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_clr -> {
                mantraCounter = 0
                supportActionBar?.title = "Nam Man Rnds: 0 0 0"
                return true
            }
            R.id.action_about -> {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView)
                } else {
                    drawerLayout.openDrawer(navigationView)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}