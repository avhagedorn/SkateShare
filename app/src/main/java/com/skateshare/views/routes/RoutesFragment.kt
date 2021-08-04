package com.skateshare.views.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentRecordBinding
import com.skateshare.databinding.FragmentRoutesBinding
import com.skateshare.db.LocalRoutesDao
import com.skateshare.services.POLYLINE_COLOR
import com.skateshare.services.POLYLINE_WIDTH
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
    private var map: GoogleMap? = null
    private var mapView: MapView? = null

    private val routes = MutableLiveData<List<LatLng>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_routes, container, false)
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        }

        displayLastRoute()
        routes.observe(viewLifecycleOwner, Observer {
            Log.i("1one", "wtf?")
            Log.i("1one", it.size.toString())
            addPolylines(it)
        })

        return binding.root
    }

    private fun displayLastRoute() {
        lifecycleScope.launch(Dispatchers.IO) {
            val route = localRoutesDao.routesByDate(1, 0).first()
            routes.postValue(toLatLng(route.lat_path, route.lng_path))
        }
    }

    private fun toLatLng(lats: List<Double>, lngs: List<Double>) : List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        for (i in lats.indices) {
            coordinates.add(LatLng(lats[i], lngs[i]))
        }
        return coordinates
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
        mapView = null         // MapView is nulled to prevent memory leak
        _binding = null
        super.onDestroyView()
    }

}