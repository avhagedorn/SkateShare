package com.skateshare.views.record

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.skateshare.R
import com.skateshare.databinding.FragmentRecordBinding

class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding get() = _binding!!

    private var _mapsFragment: SupportMapFragment? = null
    private val mapsFragment: SupportMapFragment get() = _mapsFragment!!

    private var _locationProvider: FusedLocationProviderClient? = null
    private val locationProvider get() = _locationProvider!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        _mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        _locationProvider = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapsFragment.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener { latLng ->
                Toast.makeText(requireContext(), latLng.toString(), Toast.LENGTH_SHORT).show()
            }
        }



        listenForLocationRequest()
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun listenForLocationRequest() {
        binding.beginRecording.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireActivity().applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = locationProvider.lastLocation.addOnCompleteListener { task ->
                    Toast.makeText(requireContext(),
                        "${task.result.latitude}, ${task.result.longitude}", Toast.LENGTH_SHORT).show()
                }
            } else {
                
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _locationProvider = null
        mapsFragment.onDestroyView()
    }

    // SupportMapFragment is causing a memory leak. This still does not fix it.
    // It appears this is a known issue, so unfortunately I can't do anything about it at this time.
    override fun onDestroy() {
        super.onDestroy()
        mapsFragment.onDestroy()
        _mapsFragment = null
    }
}