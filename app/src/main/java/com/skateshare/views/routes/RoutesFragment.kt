package com.skateshare.views.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentRoutesBinding
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.viewmodels.RoutesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoutesFragment : Fragment() {

    @Inject
    lateinit var localRoutesDao: LocalRoutesDao

    private var _binding: FragmentRoutesBinding? = null
    private val binding: FragmentRoutesBinding get() = _binding!!
    private lateinit var viewModel: RoutesViewModel
    private var map: GoogleMap? = null
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_routes, container, false)
        viewModel = ViewModelProvider(this).get(RoutesViewModel::class.java)

        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        viewModel.publicRoutes.observe( viewLifecycleOwner, { routes ->
            if (map != null) {
                routes.forEach { route ->
                    route.polyline.let { options ->
                        map!!.addPolyline(options)
                            .tag = route.id
                    }
                }
            }
        })

        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            goToLastPosition(savedInstanceState, RoutesFragmentArgs.fromBundle(requireArguments()))
            loadUi()

            map?.setOnCameraIdleListener {
                if (map!!.cameraPosition.zoom >= MIN_ZOOM_QUERY) {
                    viewModel.geoQueryIfNeeded(
                        currentCoordinate = map!!.cameraPosition.target,
                        zoom = map!!.cameraPosition.zoom
                    )
                }
            }

            // TODO: Add popup to route
            map?.setOnPolylineClickListener {
                Log.i("1one", it.tag.toString())
            }

        }

        viewModel.firebaseResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled && !response.isSuccessful) {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                viewModel.resetResponse()
            }
        })

        binding.tempButton.setOnClickListener {
            findNavController().navigate(
                RoutesFragmentDirections
                    .actionRoutesFragmentToPrivateRoutesFragment())
        }

        return binding.root
    }

    private fun goToLastPosition(bundle: Bundle?, argsFromPost: RoutesFragmentArgs) {
        val lat: Double?
        val lng: Double?
        if (argsFromPost.containsArgs) {
            lat = argsFromPost.lat.toDouble()
            lng = argsFromPost.lng.toDouble()
        } else {
            lat = bundle?.getDouble("lastLat")
            lng = bundle?.getDouble("lastLng")
        }

        if (lat != null && lng != null)
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lng)))
    }

    private fun loadUi() {
        binding.loading.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
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
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val lastCoordinate = map?.cameraPosition?.target ?: DEFAULT_LOCATION
        outState.putDouble("lastLat", lastCoordinate.latitude)
        outState.putDouble("lastLng", lastCoordinate.longitude)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        map = null
        mapView = null
        _binding = null
        super.onDestroyView()
    }

}