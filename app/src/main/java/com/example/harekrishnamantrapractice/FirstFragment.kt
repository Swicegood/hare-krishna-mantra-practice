package com.example.harekrishnamantrapractice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.harekrishnamantrapractice.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val packageName = context!!.packageName
        for (i in 0..0) {
            val tview = getView()?.findViewById<TextView>(R.id.textview0+i)
            tview?.textSize = 36f
            Log.d("Heeeeeeeeeeeeeeeeeeeeeeeey here is the tvew", tview.toString())
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}