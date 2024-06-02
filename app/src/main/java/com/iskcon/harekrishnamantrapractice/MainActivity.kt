package com.iskcon.harekrishnamantrapractice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.iskcon.harekrishnamantrapractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    var mantraCounter = 0
    private var switchon: Boolean = true
    private var animationManager: AnimationManager? = null
    private var speechRecognitionManager: SpeechRecognitionManager? = null

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

        navigationView.setNavigationItemSelectedListener { item ->
            val id = item.itemId
            // Handle navigation view item clicks here.
            if (id == R.id.nav_item_one) {
                // Handle the action
            } else if (id == R.id.nav_item_two) {
                mantraCounter = 0
                supportActionBar?.title = "Nam Man Rnds: 0 0 0"
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

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

        animationManager = AnimationManager(tVs)
        speechRecognitionManager = SpeechRecognitionManager(this, tVs, animationManager, ::onRecognitionResult)

        // Load SpeechResultFragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, SpeechResultFragment())
        fragmentTransaction.commit()

        binding.fab.setOnClickListener {
            switchon = !switchon
            if (switchon) {
                speechRecognitionManager?.stopListening()
                animationManager?.stopAnimation()
            } else {
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
                speechRecognitionManager?.startListening()
            }
        }
    }

    private fun onRecognitionResult(mantraCounter: Int, recognizedText: String) {
        supportActionBar?.title = "Nam Man Rnds: $mantraCounter ${mantraCounter / 16} ${mantraCounter / 1728}"

        // Update the SpeechResultFragment with the recognized text
        val speechResultFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as? SpeechResultFragment
        speechResultFragment?.updateSpeechResult(recognizedText)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}