package com.skateshare.views.routes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.skateshare.R
import com.skateshare.databinding.FragmentDetailedPublicRouteBinding
import com.skateshare.databinding.FragmentDetailedRouteBinding
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Route
import com.skateshare.models.RoutePost
import com.skateshare.viewmodels.DetailedRouteViewModel
import com.skateshare.viewmodels.PublicDetailedRouteViewModel

class DetailedPublicRouteFragment : Fragment() {

    private var _binding: FragmentDetailedPublicRouteBinding? = null
    private val binding: FragmentDetailedPublicRouteBinding get() = _binding!!
    private lateinit var viewModel: PublicDetailedRouteViewModel
    private var routeId: Long = 0L
    private lateinit var unit: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  DataBindingUtil.inflate(
            inflater, R.layout.fragment_detailed_public_route, container, false)
        viewModel = ViewModelProvider(this).get(PublicDetailedRouteViewModel::class.java)
        unit = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
            .getString("units", UNIT_MILES) ?: UNIT_MILES
        binding.unit = unit

        binding.postUsername.setOnClickListener { goToProfile() }
        binding.postUsername.setOnClickListener { goToProfile() }

        viewModel.routeData.observe(viewLifecycleOwner, {
            binding.route = it
            loadUi()
        })

        return binding.root
    }

    private fun loadUi() {
        binding.loadingContent.visibility = View.GONE
        binding.content.visibility = View.VISIBLE
    }

    private fun goToProfile() {
        val posterId = binding.route?.posterId
        findNavController().navigate(
            DetailedPublicRouteFragmentDirections
                .actionDetailedPublicRouteFragmentToProfileFragment(posterId)
        )
    }

}