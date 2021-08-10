package com.skateshare.views.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentRoutesBinding
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.viewmodels.RoutesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private var lastPosition: LatLng = LatLng(33.3, -110.0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_routes, container, false)
        viewModel = ViewModelProvider(this).get(RoutesViewModel::class.java)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        viewModel.publicRoutes.observe( viewLifecycleOwner, Observer { routes ->
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
            loadUi()

            map?.setOnCameraIdleListener {
                if (map!!.cameraPosition.zoom >= MIN_ZOOM_QUERY) {
                    viewModel.geoQueryIfNeeded(
                        currentCoordinate = map!!.cameraPosition.target,
                        zoom = map!!.cameraPosition.zoom
                    )
                }
            }

            map?.setOnPolylineClickListener {
                Log.i("1one", it.tag.toString())
            }

        }

        binding.share.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val route = localRoutesDao.routesByDate(1, 0).first()
                viewModel.publishRouteToFirestore(route, "Fun route!", LONGBOARD, MEDIUM_HILLS)
            }
        }

        viewModel.firebaseResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.status != null) {
                Toast.makeText(requireContext(), response.status, Toast.LENGTH_SHORT).show()
                viewModel.resetResponse()
            }
        })

        binding.tempButton.setOnClickListener {
            findNavController().navigate(RoutesFragmentDirections.actionRoutesFragmentToPrivateRoutesFragment())
        }

        return binding.root
    }

    private fun loadUi() {
        binding.loading.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
    }

    private fun addPolylines(coordinates: List<LatLng>) {
        for (i in 1 until coordinates.size) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(coordinates[i-1])
                .add(coordinates[i])
            map?.addPolyline(polylineOptions)
        }
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

    override fun onDestroyView() {
        mapView?.onDestroy()
        map = null
        mapView = null
        _binding = null
        super.onDestroyView()
    }

}