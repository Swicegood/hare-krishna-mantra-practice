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
    private var originalText: String = ""
    private var translatedText: String = ""
    private var isLanguageToggled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SpeechResultLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun updateSpeechResult(recognizedText: String) {
        originalText = recognizedText
        translatedText = recognizedText.replace("हरे", "Hare")
            .replace("कृष्णा", "Krishna")
            .replace("राम", "Rama")
        if (isLanguageToggled) {
            binding.speechResultTextView.text = translatedText
        } else {
            binding.speechResultTextView.text = originalText
        }
    }

    fun toggleLanguage(isToggled: Boolean) {
        binding.speechResultTextView.text = if (isToggled) translatedText else originalText
        isLanguageToggled = isToggled
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}