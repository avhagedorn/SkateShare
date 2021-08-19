package com.skateshare.views.routes

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.R
import com.skateshare.databinding.FragmentPublicRoutesBinding
import com.skateshare.misc.*
import com.skateshare.viewmodels.PublicRoutesViewModel
import com.skateshare.views.routes.publicroutesrecyclerview.PublicRoutesAdapter
import com.skateshare.views.routes.publicroutesrecyclerview.RoutePostListener

class PublicRoutesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentPublicRoutesBinding? = null
    private val binding: FragmentPublicRoutesBinding get() = _binding!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!
    private var _adapter: PublicRoutesAdapter? = null
    private val adapter: PublicRoutesAdapter get() = _adapter!!
    private lateinit var viewModel: PublicRoutesViewModel

    private lateinit var unit: String
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var radius: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_public_routes, container, false)
        viewModel = ViewModelProvider(this).get(PublicRoutesViewModel::class.java)
        initializeCoordinates(PublicRoutesFragmentArgs.fromBundle(requireArguments()))

        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        binding.sortOptions.onItemSelectedListener = this
        _recyclerView = binding.postList

        val sharedPreferences = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
        unit = sharedPreferences.getString("units", UNIT_MILES) ?: UNIT_MILES
        val avgSpeed = when (unit) {
            UNIT_MILES -> sharedPreferences.getFloat("avgSpeedMi", 0f)
            UNIT_KILOMETERS -> sharedPreferences.getFloat("avgSpeedKm", 0f)
            else -> 0f
        }
        initializeChoices()

        _adapter = PublicRoutesAdapter(RoutePostListener { lat, lng ->
            goToRouteMap(lat, lng)
        }, unit, avgSpeed)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        binding.refreshLayout.setOnRefreshListener { refreshData() }

        viewModel.hasData.observe(viewLifecycleOwner, {
            adapter.submitList(viewModel.routes)
            loadUi()
        })

        return binding.root
    }

    private fun initializeChoices() {
        var choiceArray = 0
        choiceArray = when (unit) {
            UNIT_MILES -> R.array.radius_choices_miles
            UNIT_KILOMETERS -> R.array.radius_choices_kilometers
            else -> throw Exception("Invalid units!")
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            choiceArray,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortOptions.adapter = adapter
        }
    }

    private fun goToRouteMap(lat: Float, lng: Float) {
        findNavController().navigate(
            PublicRoutesFragmentDirections.actionPublicRoutesFragmentToRoutesFragment(
                containsArgs = true,
                lat = lat,
                lng = lng
            ))
    }

    private fun loadUi() {
        binding.refreshLayout.isRefreshing = false
        binding.postsLoading.visibility = View.GONE
        binding.postList.visibility = View.VISIBLE
    }

    private fun hideUi() {
        binding.postsLoading.visibility = View.VISIBLE
        binding.postList.visibility = View.INVISIBLE
    }

    private fun initializeCoordinates(args: PublicRoutesFragmentArgs) {
        lat = args.lat.toDouble()
        lng = args.lng.toDouble()
    }

    private fun refreshData() {
        hideUi()
        viewModel.getData(lat, lng, radius)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _recyclerView = null
        _adapter = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        hideUi()
        radius = when (unit) {
            UNIT_MILES ->
                when (position) {
                    SMALL_RADIUS_POSITION -> SMALL_RADIUS_MI
                    MEDIUM_RADIUS_POSITION -> MEDIUM_RADIUS_MI
                    LARGE_RADIUS_POSITION -> LARGE_RADIUS_MI
                    else -> throw Exception("Invalid radius choice!")
            }
            UNIT_KILOMETERS ->
                when (position) {
                    SMALL_RADIUS_POSITION -> SMALL_RADIUS_KM
                    MEDIUM_RADIUS_POSITION -> MEDIUM_RADIUS_KM
                    LARGE_RADIUS_POSITION -> LARGE_RADIUS_KM
                    else -> throw Exception("Invalid radius choice!")
                }
            else -> throw Exception("Invalid units!")
        }
        viewModel.getData(lat, lng, radius)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}