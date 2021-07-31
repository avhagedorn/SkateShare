package com.skateshare.views.record

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        _mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        val locationProvider = LocationServices.getFusedLocationProviderClient(requireContext())

        mapsFragment.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener { latLng ->
                Toast.makeText(requireContext(), latLng.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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