package com.skateshare.views.record

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Camera
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentRecordBinding
import com.skateshare.misc.TrackerUtil
import com.skateshare.misc.TrackerUtil.REQUEST_CODE_LOCATION_PERMISSION
import com.skateshare.services.*
import com.skateshare.services.MapHelper.formatTime
import com.skateshare.services.MapHelper.metersToFormattedUnits
import com.skateshare.services.MapHelper.metersToStandardSpeed
import com.skateshare.services.MapHelper.metersToStandardUnits
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class RecordFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding get() = _binding!!
    private var usersUnits: String? = null
    private var map: GoogleMap? = null
    private var mapView: MapView? = null

    private var isTracking = false
    private var _route: MutableList<LatLng>? = mutableListOf<LatLng>()
    private val route: MutableList<LatLng> get() = _route!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            addAllLocations()
        }

        binding.beginRecording.setOnClickListener { startRecording() }
        binding.stopRecording.setOnClickListener { confirmStopRecording() }
        observeService()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        initializeUnits()
    }

    private fun initializeUnits() {
        usersUnits = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
            .getString("units", UNIT_MILES)
    }

    private fun updateRoute(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            showStopButton()
        } else {
            showStartButton()
            map?.clear()
        }
    }

    private fun observeService() {
        MapService.isTracking.observe(viewLifecycleOwner, Observer { newStatus ->
            updateRoute(newStatus)
        })

        MapService.routeData.observe(viewLifecycleOwner, Observer { polyline ->
            _route = polyline
            addLastLocation()
            panCameraToLastLocation()
        })

        MapService.elapsedMilliseconds.observe(viewLifecycleOwner, Observer { time ->
            binding.displayDuration.text = formatTime(time, false)
        })

        MapService.speedData.observe(viewLifecycleOwner, Observer { metersPerSecond ->
            usersUnits?.let { units ->
                if (metersPerSecond.isNotEmpty())
                    binding.displaySpeed.text = metersToStandardSpeed(metersPerSecond.last(), units)
            }
        })

        MapService.distanceMeters.observe(viewLifecycleOwner, Observer { meters ->
            usersUnits?.let { units ->
                binding.displayLength.text = metersToFormattedUnits(meters, units)
            }
        })
    }

    private fun addAllLocations() {
        val polylineOptions = PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)
            .addAll(route)
        map?.addPolyline(polylineOptions)
    }

    private fun addLastLocation() {
        if (route.size > 1) {
            val previousLatLng = route[route.size - 2]
            val newLatLng = route.last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(previousLatLng)
                .add(newLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun panCameraToLastLocation() {
        if (route.isNotEmpty())
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(route.last(), MAP_ZOOM))
    }

    private fun requestPermissions() {
        if (!TrackerUtil.hasLocationPermissions(requireContext())) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    "Location permissions are required for recording and viewing routes!",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Location permissions are required for recording and viewing routes!",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    private fun frameRoute() {
        val bounds = LatLngBounds.builder()
        for (coordinate in route) {
            bounds.include(coordinate)
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height*0.05).toInt()
            )
        )
    }

    private fun sendCommand(command: String) =
            Intent(requireContext(), MapService::class.java).also { intent ->
        intent.action = command
        requireContext().startService(intent)
    }

    private fun startRecording() = Intent(requireContext(), MapService::class.java).also { intent ->
        sendCommand(BEGIN_TRACKING)
        showStopButton()
    }

    private fun confirmStopRecording() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.finish_recording_prompt)
            .setMessage(R.string.finish_recording_warning)
            .setPositiveButton(R.string.finish) {_,_ -> stopRecording() }
            .setNegativeButton(R.string.cancel) {_,_-> /* Alert dismissed */ }
            .show()
    }

    private fun stopRecording() = Intent(requireContext(), MapService::class.java).also { intent ->
        sendCommand(STOP_TRACKING)
        showStartState()
    }

    private fun showStopButton() {
        binding.beginRecording.visibility = View.GONE
        binding.stopRecording.visibility = View.VISIBLE
    }

    private fun showStartButton() {
        binding.beginRecording.visibility = View.VISIBLE
        binding.stopRecording.visibility = View.GONE
    }

    private fun showStartState() {
        showStartButton()

        binding.displayDuration.text = getString(R.string.route_start_time)
        binding.displayLength.text = getString(R.string.route_start_distance)
        binding.displaySpeed.text = getString(R.string.route_start_speed)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        if (isTracking) showStopButton() else showStartButton()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        map = null
        mapView = null         // MapView is nulled to prevent memory leak
        _route = null
        _binding = null
        super.onDestroyView()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}