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
    private val onRecognitionResult: (Int, String, List<Int>) -> Unit,
    initialCounter: Int
) {

    private var mantraCounter = initialCounter
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
            var translatedRecognizedText = ""

            resultList?.forEach { result ->
                val words = result.split(' ')
                words.forEach { word ->
                    if (isMantraWord(word)) {
                        mantraCounter++
                    }
                }

                val translatedRecognizedText = result.replace("हरे", "Hare")
                    .replace("कृष्णा", "Krsna")
                    .replace("राम", "Rama")

                val mantraWords = listOf("Hare", "Krsna", "Hare", "Krsna", "Krsna", "Krsna", "Hare", "Hare", "Hare", "Rama", "Hare", "Rama", "Rama", "Rama", "Hare", "Hare")
                var recognizedWords = translatedRecognizedText.split(" ")

                if (recognizedWords.size > mantraWords.size) {
                    recognizedWords = recognizedWords.subList(0, mantraWords.size)
                } else if (recognizedWords.size < mantraWords.size) {
                    recognizedWords += List(mantraWords.size - recognizedWords.size) { "" }
                }

                // Align the mantra and the recognized words
                val (alignedMantra, alignedRecognition) = needlemanWunsch(mantraWords, recognizedWords)

                // Identify missing words
                val missingWordsIndices = alignedMantra.indices.filter { alignedMantra[it] != alignedRecognition[it] }

                // Call back to update the UI
                onRecognitionResult(mantraCounter, recognizedText, missingWordsIndices)

                // If recognizedWords is twice as long, process it in two segments
                if (recognizedWords.size > mantraWords.size && recognizedWords.size <= 2 * mantraWords.size) {
                    val firstSegment = recognizedWords.subList(0, mantraWords.size)
                    val (alignedMantra1, alignedRecognition1) = needlemanWunsch(mantraWords, firstSegment)
                    val missingWordsIndices1 = alignedMantra1.indices.filter { alignedMantra1[it] != alignedRecognition1[it] }
                    onRecognitionResult(mantraCounter, recognizedText, missingWordsIndices1)

                    val secondSegment = recognizedWords.subList(mantraWords.size, recognizedWords.size)
                    val (alignedMantra2, alignedRecognition2) = needlemanWunsch(mantraWords, secondSegment)
                    val missingWordsIndices2 = alignedMantra2.indices.filter { alignedMantra2[it] != alignedRecognition2[it] }
                    onRecognitionResult(mantraCounter, recognizedText, missingWordsIndices2)
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
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    showError("No speech input. Please try again.")
                    animationManager?.stopAnimation()
                }
                SpeechRecognizer.ERROR_NO_MATCH -> {
                    showError("No Holy Names heard. Please try again.")
                    animationManager?.stopAnimation()
                }
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
    fun needlemanWunsch(seq1: List<String>, seq2: List<String>): Pair<List<String>, List<String>> {
        val n = seq1.size
        val m = seq2.size
        val score = Array(n + 1) { IntArray(m + 1) }
        val traceback = Array(n + 1) { IntArray(m + 1) }

        for (i in 0..n) {
            score[i][0] = -i
            traceback[i][0] = 1
        }
        for (j in 0..m) {
            score[0][j] = -j
            traceback[0][j] = 2
        }

        for (i in 1..n) {
            for (j in 1..m) {
                val match = score[i - 1][j - 1] + if (seq1[i - 1] == seq2[j - 1]) 1 else -1
                val delete = score[i - 1][j] - 1
                val insert = score[i][j - 1] - 1
                score[i][j] = maxOf(match, delete, insert)
                traceback[i][j] = when (score[i][j]) {
                    match -> 0
                    delete -> 1
                    insert -> 2
                    else -> throw IllegalStateException()
                }
            }
        }

        var alignedSeq1 = mutableListOf<String>()
        var alignedSeq2 = mutableListOf<String>()
        var i = n
        var j = m

        while (i > 0 || j > 0) {
            when (traceback[i][j]) {
                0 -> {
                    alignedSeq1.add(seq1[i - 1])
                    alignedSeq2.add(seq2[j - 1])
                    i -= 1
                    j -= 1
                }
                1 -> {
                    alignedSeq1.add(seq1[i - 1])
                    alignedSeq2.add("-")
                    i -= 1
                }
                2 -> {
                    alignedSeq1.add("-")
                    alignedSeq2.add(seq2[j - 1])
                    j -= 1
                }
            }
        }

        alignedSeq1.reverse()
        alignedSeq2.reverse()

        return Pair(alignedSeq1, alignedSeq2)
    }
}