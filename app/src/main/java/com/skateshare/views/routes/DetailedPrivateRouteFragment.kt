package com.skateshare.views.routes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.skateshare.R
import com.skateshare.databinding.FragmentDetailedRouteBinding
import com.skateshare.misc.POLYLINE_COLOR
import com.skateshare.misc.POLYLINE_WIDTH
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Route
import com.skateshare.viewmodels.DetailedRouteViewModel
import com.skateshare.views.profile.ProfileFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedPrivateRouteFragment : Fragment() {

    private var _binding: FragmentDetailedRouteBinding? = null
    private val binding: FragmentDetailedRouteBinding get() = _binding!!
    private lateinit var viewModel: DetailedRouteViewModel
    private var routeId: Long = 0L
    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private lateinit var unit: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detailed_route, container, false)
        routeId = DetailedPrivateRouteFragmentArgs.fromBundle(requireArguments()).routeId
        unit = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
            .getString("units", UNIT_MILES) ?: UNIT_MILES
        binding.unit = unit
        binding.route = Route() // TODO: Examine this

        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { providedMap ->
            map = providedMap
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            map?.mapType = GoogleMap.MAP_TYPE_NORMAL

            map?.setOnMapLoadedCallback {
                showMap()
            }

            viewModel = ViewModelProvider(this).get(DetailedRouteViewModel::class.java)
            viewModel.getRoute(routeId)
            viewModel.routeData.observe(viewLifecycleOwner, { route ->
                binding.route = route
                val lineDataSet = viewModel.getSpeedData()
                removeChartDetail()
                binding.speedChart.data = getStyledLineData(lineDataSet)
                binding.speedChart.invalidate()
                binding.executePendingBindings()
                loadUi(route.isPublic)
                frameRoute(route)
            })

        }

        binding.share.setOnClickListener {
            shareRoute()
        }

        return binding.root
    }

    private fun shareRoute() {
        findNavController().navigate(
            DetailedPrivateRouteFragmentDirections
                .actionDetailedPrivateRouteFragmentToShareRouteFragment(binding.route!!.id))
    }

    private fun getStyledLineData(data : LineDataSet) = LineData(
            data.apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                fillColor = ContextCompat.getColor(requireContext(), R.color.red_500)
                color = ContextCompat.getColor(requireContext(), R.color.red_500)
                fillAlpha = 235
                setDrawFilled(true)
                setDrawValues(false)
                setDrawCircles(false)
            }
        )

    private fun removeChartDetail() {
        binding.speedChart.apply {
            description.text = ""
            xAxis.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            axisRight.setDrawLabels(false)
        }
    }

    private fun frameRoute(route: Route) {
        val bounds = LatLngBounds.builder()
        route.let { it ->
            val lats = it.lat_path
            val lngs = it.lng_path
            val path = mutableListOf<LatLng>()

            for (i in it.lat_path.indices) {
                path.add(LatLng(lats[i], lngs[i]))
                bounds.include(path[i])
            }

            map?.addPolyline(PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(path)
            )

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

    private fun showMap() {
        binding.mapLoading.visibility = View.GONE
        binding.mapView.visibility = View.VISIBLE
    }

    private fun loadUi(hasBeenShared: Boolean) {

        if (hasBeenShared)
            binding.share.visibility = View.GONE

        binding.loading.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
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
        super.onDestroyView()
        mapView?.onDestroy()
    }
}