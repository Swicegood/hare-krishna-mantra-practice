package com.iskcon.harekrishnamantrapractice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat

class BluetoothAudioManager(private val context: Context) {
    
    var onConnectionStateChanged: (() -> Unit)? = null
    
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothHeadset: BluetoothHeadset? = null
    private var isBluetoothConnected = false
    private var bluetoothProfileListener: BluetoothProfile.ServiceListener? = null
    
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED)
                    val device = intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                    isBluetoothConnected = state == BluetoothProfile.STATE_CONNECTED
                    Log.d("BluetoothAudioManager", "Bluetooth headset connection state changed: $state for device: $device")
                    
                    // Also check connected devices from the headset proxy
                    val connectedDevices = if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        bluetoothHeadset?.connectedDevices
                    } else {
                        Log.w("BluetoothAudioManager", "BLUETOOTH_CONNECT permission not granted")
                        null
                    }
                    Log.d("BluetoothAudioManager", "Currently connected headset devices: $connectedDevices")
                    onConnectionStateChanged?.invoke()
                }
                AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                    val state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_DISCONNECTED)
                    val stateText = when (state) {
                        AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> "DISCONNECTED"
                        AudioManager.SCO_AUDIO_STATE_CONNECTING -> "CONNECTING"
                        AudioManager.SCO_AUDIO_STATE_CONNECTED -> "CONNECTED"
                        AudioManager.SCO_AUDIO_STATE_ERROR -> "ERROR"
                        else -> "UNKNOWN($state)"
                    }
                    Log.d("BluetoothAudioManager", "SCO audio state changed: $stateText ($state)")
                    Log.d("BluetoothAudioManager", "Current SCO on status: ${audioManager.isBluetoothScoOn}")
                    onConnectionStateChanged?.invoke()
                }
            }
        }
    }
    
    init {
        Log.d("BluetoothAudioManager", "Initializing BluetoothAudioManager")
        setupBluetoothProfileListener()
        registerBluetoothReceiver()
        connectBluetoothProfile()
        Log.d("BluetoothAudioManager", "BluetoothAudioManager initialization complete")
    }
    
    private fun setupBluetoothProfileListener() {
        bluetoothProfileListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = proxy as BluetoothHeadset
                    Log.d("BluetoothAudioManager", "Bluetooth headset service connected")
                    
                    // Check for connected devices
                    val connectedDevices = if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        bluetoothHeadset?.connectedDevices
                    } else {
                        Log.w("BluetoothAudioManager", "BLUETOOTH_CONNECT permission not granted")
                        null
                    }
                    Log.d("BluetoothAudioManager", "Connected headset devices: $connectedDevices")
                    
                    if (connectedDevices?.isNotEmpty() == true) {
                        isBluetoothConnected = true
                        Log.d("BluetoothAudioManager", "Bluetooth headset is connected")
                    } else {
                        Log.d("BluetoothAudioManager", "No Bluetooth headset devices connected")
                    }
                    onConnectionStateChanged?.invoke()
                }
            }
            
            override fun onServiceDisconnected(profile: Int) {
                if (profile == BluetoothProfile.HEADSET) {
                    bluetoothHeadset = null
                    isBluetoothConnected = false
                    Log.d("BluetoothAudioManager", "Bluetooth headset service disconnected")
                }
            }
        }
    }
    
    private fun registerBluetoothReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
            addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)
        }
        context.registerReceiver(bluetoothStateReceiver, filter)
    }
    
    private fun connectBluetoothProfile() {
        Log.d("BluetoothAudioManager", "Connecting to Bluetooth profile...")
        val result = bluetoothAdapter?.getProfileProxy(context, bluetoothProfileListener, BluetoothProfile.HEADSET)
        Log.d("BluetoothAudioManager", "getProfileProxy result: $result")
    }
    
    fun isBluetoothAudioAvailable(): Boolean {
        val adapterEnabled = bluetoothAdapter?.isEnabled == true
        Log.d("BluetoothAudioManager", "isBluetoothAudioAvailable: adapterEnabled=$adapterEnabled, isBluetoothConnected=$isBluetoothConnected")
        Log.d("BluetoothAudioManager", "bluetoothAdapter: $bluetoothAdapter")
        Log.d("BluetoothAudioManager", "bluetoothHeadset: $bluetoothHeadset")
        return adapterEnabled && isBluetoothConnected
    }
    
    fun startBluetoothSco() {
        Log.d("BluetoothAudioManager", "startBluetoothSco called - isBluetoothAudioAvailable: ${isBluetoothAudioAvailable()}")
        Log.d("BluetoothAudioManager", "Bluetooth adapter enabled: ${bluetoothAdapter?.isEnabled}")
        Log.d("BluetoothAudioManager", "Bluetooth connected: $isBluetoothConnected")
        
        if (isBluetoothAudioAvailable()) {
            try {
                audioManager.startBluetoothSco()
                Log.d("BluetoothAudioManager", "Started Bluetooth SCO successfully")
            } catch (e: SecurityException) {
                Log.e("BluetoothAudioManager", "Security exception starting Bluetooth SCO: ${e.message}")
            }
        } else {
            Log.w("BluetoothAudioManager", "Cannot start Bluetooth SCO - audio not available")
        }
    }
    
    fun stopBluetoothSco() {
        if (audioManager.isBluetoothScoOn) {
            audioManager.stopBluetoothSco()
            Log.d("BluetoothAudioManager", "Stopped Bluetooth SCO")
        }
    }
    
    fun isBluetoothScoOn(): Boolean {
        val scoOn = audioManager.isBluetoothScoOn
        Log.d("BluetoothAudioManager", "isBluetoothScoOn: $scoOn")
        return scoOn
    }
    
    fun getAudioSource(): Int {
        return when {
            isBluetoothAudioAvailable() -> MediaRecorder.AudioSource.MIC
            else -> MediaRecorder.AudioSource.MIC
        }
    }
    
    fun cleanup() {
        try {
            context.unregisterReceiver(bluetoothStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.w("BluetoothAudioManager", "Receiver was not registered", e)
        }
        
        bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
        bluetoothHeadset = null
    }
}