package com.skateshare.views.routes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.skateshare.models.RouteGlobalMap
import com.skateshare.viewmodels.routes.RoutesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RoutesFragment : Fragment() {

    @Inject
    lateinit var localRoutesDao: LocalRoutesDao
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentRoutesBinding? = null
    private val binding: FragmentRoutesBinding get() = _binding!!
    private lateinit var viewModel: RoutesViewModel
    private var map: GoogleMap? = null
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_routes, container, false)
        viewModel = ViewModelProvider(this).get(RoutesViewModel::class.java)
        sharedPreferences = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            goToLastPosition(RoutesFragmentArgs.fromBundle(requireArguments()))

            map?.setOnMapLoadedCallback {
                loadUi()
            }

            map?.setOnCameraIdleListener {
                queryIfNeeded()
            }

            map?.setOnPolylineClickListener {
                findNavController().navigate(
                    RoutesFragmentDirections.
                    actionRoutesFragmentToDetailedPublicRouteFragment(it.tag.toString())
                )
            }

            map?.setOnMapLongClickListener { coordinate ->
                val lat = coordinate.latitude.toFloat()
                val lng = coordinate.longitude.toFloat()
                findNavController().navigate(
                    RoutesFragmentDirections.
                        actionRoutesFragmentToPublicRoutesFragment(lat, lng)
                )
            }

            viewModel.publicRoutes.observe(viewLifecycleOwner, { routes ->
                addRoutes(routes)
            })
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

    private fun addRoutes(routes: List<RouteGlobalMap>) {
        routes.forEach { route ->
            route.polyline.let { options ->
                map?.addPolyline(options)?.apply {
                    tag = route.id
                }
            }
        }
    }

    private fun queryIfNeeded() {
        if (map!!.cameraPosition.zoom >= MIN_ZOOM_QUERY) {
            viewModel.geoQueryIfNeeded(
                currentCoordinate = map!!.cameraPosition.target,
                zoom = map!!.cameraPosition.zoom
            )
        }
    }

    private fun goToLastPosition(argsFromPost: RoutesFragmentArgs) {
        val lat: Double?
        val lng: Double?
        val zoom: Float?
        if (argsFromPost.containsArgs) {
            lat = argsFromPost.lat.toDouble()
            lng = argsFromPost.lng.toDouble()
            zoom = MAP_ZOOM
        } else {
            lat = sharedPreferences.getFloat("lastLat", 0f).toDouble()
            lng = sharedPreferences.getFloat("lastLng", 0f).toDouble()
            zoom = sharedPreferences.getFloat("lastZoom", 2f)
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom))
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
        val pos = map?.cameraPosition?.target ?: DEFAULT_LOCATION
        sharedPreferences.edit()
            .putFloat("lastLat", pos.latitude.toFloat())
            .putFloat("lastLng", pos.longitude.toFloat())
            .putFloat("lastZoom", map?.cameraPosition?.zoom ?: MAP_ZOOM)
            .apply()
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
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        map?.clear()
        mapView?.onDestroy()
        map = null
        mapView = null
        _binding = null
        super.onDestroyView()
    }

}