package com.iskcon.harekrishnamantrapractice

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.iskcon.harekrishnamantrapractice.databinding.FragmentFirstBinding

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


        val tvIDs = arrayOf<Int>(R.id.textview0,
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

        for (element in tvIDs){
            var tview = getView()?.findViewById<TextView>(element)
            tview?.textSize = 36f
            Log.d("Heeeeeeeeeeeeeeeeeeeeeeeey here is the tvew", tview.toString())
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}