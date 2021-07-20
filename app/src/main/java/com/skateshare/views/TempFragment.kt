package com.skateshare.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.skateshare.R

class TempFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_temp, container, false)

        view.findViewById<Button>(R.id.btn).setOnClickListener{
            findNavController().navigate(TempFragmentDirections.actionTempFragmentToProfileFragment(null))
        }

        view.findViewById<Button>(R.id.btn2).setOnClickListener {
            findNavController().navigate(TempFragmentDirections.actionTempFragmentToProfileFragment("OWgWh9PD8Ad9Yi6KBuUXVSFEOJY2"))
        }

        view.findViewById<Button>(R.id.btn3).setOnClickListener {
            // findNavController().navigate(TempFragmentDirections.actionTempFragmentToProfileFragment("LrnacDNXndgQ5r1zqWnlTJFnQW83"))
        }

        view.findViewById<Button>(R.id.btn4).setOnClickListener {
            findNavController().navigate(TempFragmentDirections.actionTempFragmentToProfileFragment("1pq2qiMbDzMUy1ViWI6Eyr73dLL2"))
        }

        return view
    }

 }