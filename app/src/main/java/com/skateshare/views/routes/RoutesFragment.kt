package com.skateshare.views.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentRecordBinding
import com.skateshare.databinding.FragmentRoutesBinding
import com.skateshare.db.LocalRoutesDao
import com.skateshare.models.Route
import com.skateshare.services.POLYLINE_COLOR
import com.skateshare.services.POLYLINE_WIDTH
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
    private lateinit var route: Route
    private var map: GoogleMap? = null
    private var mapView: MapView? = null

    private val routes = MutableLiveData<List<LatLng>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_routes, container, false)
        viewModel = ViewModelProvider(this).get(RoutesViewModel::class.java)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))

            map?.setOnCameraMoveListener {
                val zoom = map!!.cameraPosition.zoom.toDouble()
                val center = map!!.cameraPosition.target
                viewModel.geoQueryAbout(center, zoom)
            }
        }

        displayLastRoute()
        routes.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                addPolylines(it)
                frameRoute()
            }
        })

        binding.share.setOnClickListener {
            viewModel.publishRouteToFirestore(route)
        }

        viewModel.firebaseResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.status != null) {
                Toast.makeText(requireContext(), response.status, Toast.LENGTH_SHORT).show()
                viewModel.resetResponse()
            }
        })

        // Temporary
        viewModel.publicRoutes.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it.size.toString(), Toast.LENGTH_SHORT).show()
        })

        return binding.root
    }

    private fun displayLastRoute() {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val query = localRoutesDao.routesByDate(1, 0)
                if (query.isNotEmpty()) {
                    route = query.first()
                    routes.postValue(viewModel.toLatLng(route.lat_path, route.lng_path))
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_LONG).show()
        }
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

    private fun frameRoute() {
        val bounds = LatLngBounds.builder()
        routes.value?.let { coordinates ->
            for (coordinate in coordinates) {
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
        mapView = null         // MapView is nulled to prevent memory leak
        _binding = null
        super.onDestroyView()
    }

}