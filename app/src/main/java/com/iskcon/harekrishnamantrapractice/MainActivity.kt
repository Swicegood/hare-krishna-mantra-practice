package com.iskcon.harekrishnamantrapractice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.iskcon.harekrishnamantrapractice.databinding.ActivityMainBinding

const val PREFS_NAME = "mantra_prefs"
const val MANTRA_COUNTER_KEY = "mantra_counter"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private var isLanguageToggled: Boolean = false
    var nameCounter = 0
    private var switchon: Boolean = true
    private var isListening: Boolean = false
    private var animationManager: AnimationManager? = null
    private var speechRecognitionManager: SpeechRecognitionManager? = null
    private lateinit var audioManager: AudioManager
    private var previousVolume: Int = 0
    private val tvIDs = arrayOf(
        R.id.textview0, R.id.textview1, R.id.textview2, R.id.textview3,
        R.id.textview4, R.id.textview5, R.id.textview6, R.id.textview7,
        R.id.textview8, R.id.textview9, R.id.textview10, R.id.textview11,
        R.id.textview12, R.id.textview13, R.id.textview14, R.id.textview15
    )

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
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up the navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this)
        Log.d("MainActivity", "Navigation item selected listener set")

        // Set up the navigation controller
        val navController = findNavController(R.id.fragment_container_view)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        nameCounter = loadMantraCounter()
        supportActionBar?.title = "Mantra Counter: ${nameCounter/16}"
        Log.d("MainActivity", "Loaded mantra counter: $nameCounter")

        val tVs = tvIDs.map { binding.root.findViewById<TextView>(it) }.toTypedArray()

        // Load the custom font
        val typeface = ResourcesCompat.getFont(this, R.font.tangerine_bold) ?: Typeface.DEFAULT

        // Apply the custom font to each TextView
        tVs.forEach { tv ->
            tv.typeface = typeface
            tv.textSize = 34f
        }
        // Initialize AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        animationManager = AnimationManager(tVs)
        speechRecognitionManager = SpeechRecognitionManager(this, tVs, animationManager, ::onRecognitionResult, initialCounter = nameCounter)

        // Load SpeechResultFragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, SpeechResultFragment())
        fragmentTransaction.commit()

        val speedSeekBar = binding.root.findViewById<SeekBar>(R.id.speedSeekBar)
        speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Map progress to speed with max speed at 90% of the slider
                val maxSpeed = 200 // fastest speed in milliseconds
                val minSpeed = 1000 // slowest speed in milliseconds
                val effectiveProgress = ((1000 - progress) / 900.0) * (minSpeed - maxSpeed) + maxSpeed

                // Update the animation speed
                animationManager?.updateAnimationSpeed(effectiveProgress.toInt())
                Log.d("MainActivity", "SeekBar progress changed: $progress, effective speed: $effectiveProgress")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })


        binding.fab.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
            updateFabIcon()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("MainActivity", "Navigation item selected: ${item.itemId}")
        when (item.itemId) {
            R.id.toggle_language -> {
                // Toggle the language
                isLanguageToggled = !isLanguageToggled
                Log.d("MainActivity", "Language toggled: $isLanguageToggled")
                // Show a message to the user
                val message = if (isLanguageToggled) {
                    getString(R.string.language_changed_to_english)
                } else {
                    getString(R.string.language_changed_to_sanskrit)
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Update the text in the fragment
                val speechResultFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as? SpeechResultFragment
                speechResultFragment?.toggleLanguage(isLanguageToggled)
            }
            R.id.clear_counters -> {
                // Handle clear counters action
                nameCounter = 0
                supportActionBar?.title = "Correct Mantras: 0"
                saveMantraCounter(nameCounter)
                Toast.makeText(this, "Counters cleared", Toast.LENGTH_SHORT).show()
            }
            else -> Log.d("MainActivity", "Unknown menu item selected: ${item.itemId}")
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onRecognitionResult(mantraCounter: Int, recognizedText: String, missingWordsIndices: List<Int>) {
        supportActionBar?.title = "Correct Mantras: ${mantraCounter / 16}"
        saveMantraCounter(mantraCounter)

        // Update the SpeechResultFragment with the recognized text
        val speechResultFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as? SpeechResultFragment
        speechResultFragment?.updateSpeechResult(recognizedText)

        val mantraWords = listOf("Hare", "Krsna", "Hare", "Krsna", "Krsna", "Krsna", "Hare", "Hare", "Hare", "Rama", "Hare", "Rama", "Rama", "Rama", "Hare", "Hare")
        for (i in mantraWords.indices) {
            if (i < tvIDs.size) {
                val tv = findViewById<TextView>(tvIDs[i])
                if (missingWordsIndices.contains(i)) {
                    tv.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                } else {
                    tv.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                }
            }
        }

        // Reset the color after 1 second
        Handler(Looper.getMainLooper()).postDelayed({
            for (i in missingWordsIndices) {
                if (i < tvIDs.size) {
                    val tv = findViewById<TextView>(tvIDs[i])
                    tv.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                }
            }
        }, 2000)
    }

    private fun muteSystemVolume() {
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
    }

    private fun restoreSystemVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, previousVolume, 0)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container_view)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun saveMantraCounter(counter: Int) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(MANTRA_COUNTER_KEY, counter)
        editor.apply()
    }

    private fun loadMantraCounter(): Int {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return sharedPreferences.getInt(MANTRA_COUNTER_KEY, 0)
    }

    private fun startListening() {
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
            return
        }
        muteSystemVolume()
        speechRecognitionManager?.startListening()
        animationManager?.startAnimation()
        isListening = true
    }

    private fun stopListening() {
        speechRecognitionManager?.stopListening()
        animationManager?.stopAnimation()
        restoreSystemVolume()
        isListening = false
    }

    private fun updateFabIcon() {
        binding.fab.setImageResource(
            if (isListening) R.drawable.ic_clear_counters
            else R.drawable.ic_baseline_mic_24
        )
    }

}
