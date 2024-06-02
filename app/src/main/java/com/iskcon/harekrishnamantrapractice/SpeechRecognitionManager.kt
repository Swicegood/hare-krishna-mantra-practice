package com.iskcon.harekrishnamantrapractice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class SpeechRecognitionManager(
    private val context: Context,
    private val tVs: Array<TextView>,
    private val animationManager: AnimationManager?,
    private val onRecognitionResult: (Int, String) -> Unit
) {

    private var mantraCounter = 0
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Toast.makeText(context, "Listening...", Toast.LENGTH_SHORT).show()
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            Log.d("SpeechRecognition", "Error: $error")
            if (error != SpeechRecognizer.ERROR_NO_MATCH) {
                recognizer.startListening(intent)
            }
            handleError(error)
        }

        override fun onResults(results: Bundle?) {
            val resultList = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val recognizedText = resultList?.joinToString(separator = " ") ?: ""

            resultList?.forEach { result ->
                result.split(' ').forEach { word ->
                if (isMantraWord(word)) {
                    mantraCounter++
                    onRecognitionResult(mantraCounter, recognizedText.replace("हरे", "Hare")
                        .replace("कृष्णा", "Krishna")
                        .replace("राम", "Rama"))
                }
                }
            }
            Log.d("SpeechRecognition", "Results: $results")
            recognizer.startListening(intent)
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val resultList =
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            resultList?.forEach { result ->
                if (result.contains("Krishna", ignoreCase = true)) {
                    mantraCounter++
                }
            }
            Log.d("SpeechRecognition", "Partial results: $partialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        private fun isMantraWord(word: String): Boolean {
            return word.contains("Krishna", ignoreCase = true) ||
                    word.contains("hurry", ignoreCase = true) ||
                    word.contains("Hare", ignoreCase = true) ||
                    word.contains("hari", ignoreCase = true) ||
                    word.contains("हरे", ignoreCase = true) ||
                    word.contains("ram", ignoreCase = true) ||
                    word.contains("Merry", ignoreCase = true) ||
                    word.contains("Christmas", ignoreCase = true) ||
                    word.contains("Christian", ignoreCase = true) ||
                    word.contains("कृष्णा", ignoreCase = true) ||
                    word.contains("Snickers", ignoreCase = true) ||
                    word.contains("hurray", ignoreCase = true) ||
                    word.contains("today", ignoreCase = true) ||
                    word.contains("headache", ignoreCase = true) ||
                    word.contains("राम", ignoreCase = true) ||
                    word.contains("rama", ignoreCase = true)
        }

        private fun handleError(error: Int) {
            when (error) {
                SpeechRecognizer.ERROR_NETWORK -> showError("Network error. Please check your internet connection.")
                SpeechRecognizer.ERROR_AUDIO -> showError("Audio error. Please try again.")
                SpeechRecognizer.ERROR_SERVER -> showError("Server error. Please try again later.")
                SpeechRecognizer.ERROR_CLIENT -> showError("Client error. Please try again.")
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> showError("No speech input. Please try again.")
                SpeechRecognizer.ERROR_NO_MATCH -> showError("No Holy Names heard. Please try again.")
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> showError("Recognition service is busy. Please try again later.")
            }
        }

        private fun showError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    init {
        recognizer.setRecognitionListener(listener)
    }

    fun startListening() {
        recognizer.startListening(intent)
        animationManager?.startAnimation()
    }

    fun stopListening() {
        recognizer.stopListening()
    }
}