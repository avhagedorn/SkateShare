package com.skateshare.views.routes

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.Timestamp
import com.skateshare.R
import com.skateshare.databinding.FragmentDetailedPublicRouteBinding
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.RoutePost
import com.skateshare.viewmodels.routes.PublicDetailedRouteViewModel

class DetailedPublicRouteFragment : Fragment() {

    private var _binding: FragmentDetailedPublicRouteBinding? = null
    private val binding: FragmentDetailedPublicRouteBinding get() = _binding!!
    private lateinit var viewModel: PublicDetailedRouteViewModel
    private lateinit var routeId: String
    private lateinit var unit: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  DataBindingUtil.inflate(
            inflater, R.layout.fragment_detailed_public_route, container, false)
        routeId = DetailedPublicRouteFragmentArgs.fromBundle(requireArguments()).routeId
        viewModel = ViewModelProvider(this).get(PublicDetailedRouteViewModel::class.java)
        viewModel.getRoutePost(routeId)

        val sharedPreferences = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
        unit = sharedPreferences.getString("units", UNIT_MILES) ?: UNIT_MILES
        val avgSpeed = when (unit) {
            UNIT_MILES -> sharedPreferences.getFloat("avgSpeedMi", 0f)
            UNIT_KILOMETERS -> sharedPreferences.getFloat("avgSpeedKm", 0f)
            else -> 0f
        }

        binding.unit = unit
        binding.route = RoutePost()
        binding.avgSpeed = avgSpeed
        binding.executePendingBindings()

        binding.postUsername.setOnClickListener { goToProfile() }
        binding.postUsername.setOnClickListener { goToProfile() }

        viewModel.routeData.observe(viewLifecycleOwner, {
            binding.route = it
            loadUi()
            loadImage()
        })

        viewModel.firebaseResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled && !response.isSuccessful) {
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                viewModel.resetResponse()
            }
        })

        return binding.root
    }

    private fun loadImage() {
        val url = binding.route?.imgUrl
        if (url != null) {
            val postImage = binding.postImage
            Glide.with(postImage.context)
                .load(url)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean) : Boolean {
                        binding.loading.visibility = View.GONE
                        Toast.makeText(requireContext(), e?.message, Toast.LENGTH_SHORT).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.loading.visibility = View.GONE
                        return false
                    }
                }).into(postImage)
        } else {
            binding.loading.visibility = View.GONE
            binding.postImage.visibility = View.GONE
        }
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