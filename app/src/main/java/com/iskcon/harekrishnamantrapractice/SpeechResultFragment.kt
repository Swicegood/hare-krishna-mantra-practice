package com.iskcon.harekrishnamantrapractice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iskcon.harekrishnamantrapractice.databinding.SpeechResultLayoutBinding

class SpeechResultFragment : Fragment() {

    private var _binding: SpeechResultLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SpeechResultLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example: Setting speech recognition result to TextView
        val speechRecognitionResult = "Speech recognition result will appear here."
        binding.speechResultTextView.text = speechRecognitionResult
    }

    fun updateSpeechResult(recognizedText: String) {
        binding.speechResultTextView.text = recognizedText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}