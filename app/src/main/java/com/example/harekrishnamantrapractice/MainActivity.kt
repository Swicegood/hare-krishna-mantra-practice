package com.example.harekrishnamantrapractice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.harekrishnamantrapractice.databinding.ActivityMainBinding
import com.example.harekrishnamantrapractice.databinding.FragmentFirstBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        var tVs = arrayOf<TextView?>()

        var tvIDs = arrayOf<Int>(R.id.textview0,
            R.id.textview1,
            R.id.textview2,
            R.id.textview3,
            R.id.textview4,
            R.id.textview5,
            R.id.textview6,
            R.id.textview7,
            R.id.textview7,
            R.id.textview8,
            R.id.textview9,
            R.id.textview10,
            R.id.textview11,
            R.id.textview12,
            R.id.textview13,
            R.id.textview14,
            R.id.textview15)

        for (element in tvIDs) {
            var tview = binding.root.findViewById<TextView>(element)
            tVs += tview
        }

        binding.fab.setOnClickListener {

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

            var mantraCounter = 0 // Step 1

            val listener = object : RecognitionListener {


                override fun onReadyForSpeech(params: Bundle?) {
                    // Do something when the speech input is ready
                }

                override fun onBeginningOfSpeech() {
                    // Do something when the speech input has started
                }

                override fun onRmsChanged(p0: Float) {
                    // do something
                }

                override fun onBufferReceived(p0: ByteArray?) {
                    // do something
                }

                override fun onEndOfSpeech() {
                    // do something
                }

                override fun onError(p0: Int) {
                    if (mantraCounter < 20) {
                        recognizer.startListening(intent)
                    }
                    Log.d("Hey this is Jaga, the Error Number is ", p0.toString())
                }

                override fun onResults(p0: Bundle?) {
                    val results = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (results != null) {
                        for (result in results) {
                            val words = result.split(" ") // Step 2
                            for (word in words) {
                                if (word.contains("Krishna", ignoreCase = true) ||
                                    word.contains("hurry", ignoreCase = true) ||
                                    word.contains("Hare", ignoreCase = true) ||
                                    word.contains("hari", ignoreCase = true) ||
                                    word.contains("hottie", ignoreCase = true) ||
                                    word.contains("ram", ignoreCase = true) ||
                                    word.contains("Merry", ignoreCase = true) ||
                                    word.contains("Christmas", ignoreCase = true) ||
                                    word.contains("Christian", ignoreCase = true) ||
                                    word.contains("krishnaker", ignoreCase = true) ||
                                    word.contains("Snickers", ignoreCase = true) ||
                                    word.contains("hurray", ignoreCase = true) ||
                                    word.contains("today", ignoreCase = true) ||
                                    word.contains("headache", ignoreCase = true) ||
                                    word.contains("rama", ignoreCase = true)) {
                                    tVs[mantraCounter % 16]?.setTypeface(null, Typeface.BOLD)
                                    mantraCounter++
                                    if ((mantraCounter % 16) == 0){
                                        getSupportActionBar()?.setTitle("Mantras Chanted: "+mantraCounter.toString()+" Rounds: "+(mantraCounter/108).toString())
                                        for (element in tVs){
                                            element?.setTypeface(null, Typeface.NORMAL)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Log.d("Hey we got results", mantraCounter.toString())
                    Log.d("We are listening. We have received results!", p0.toString())
                    recognizer.startListening(intent)


                }

                override fun onPartialResults(p0: Bundle?) {
                    val results = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (results != null) {
                        for (result in results) {
                            if (result.contains("Krishna")) { // Step 2
                                mantraCounter++

                            }
                        }
                    }
                    Log.d("We are listening. We have received results!", p0.toString())
                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                    //do
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
