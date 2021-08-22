package com.skateshare.views.routes

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentShareRouteBinding
import com.skateshare.viewmodels.routes.ShareRouteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareRouteFragment : Fragment() {

    private var _binding: FragmentShareRouteBinding? = null
    private val binding: FragmentShareRouteBinding get() = _binding!!
    private lateinit var viewModel: ShareRouteViewModel
    private var imageUri: Uri? = null
    private var routeId: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_route, container, false)
        routeId = ShareRouteFragmentArgs.fromBundle(requireArguments()).routeId
        viewModel = ViewModelProvider(this).get(ShareRouteViewModel::class.java)

        binding.post.setOnClickListener { submitPost() }
        binding.cancel.setOnClickListener { returnToDetailedView() }

        viewModel.postResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled) {
                if (response.isSuccessful) {
                    Snackbar.make(
                        requireView(), R.string.share_route_success, Snackbar.LENGTH_SHORT).show()
                    returnToDetailedView()
                } else {
                    Toast.makeText(
                        requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
                viewModel.resetResponse()
            }
        })

        setImageUploadListener()
        setupFormChoices()
        return binding.root
    }

    private fun setImageUploadListener() {
        val getRouteImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.postImage)
                imageUri = uri
            }
        }

        binding.postImage.setOnClickListener { getRouteImage.launch("image/*") }
    }

    private fun submitPost() {
        binding.loading.visibility = View.VISIBLE
        viewModel.submitPost(
            routeId = routeId,
            routeDescription = binding.description.text.toString(),
            boardType = binding.menuBoardType.editText?.text.toString(),
            terrainType = binding.menuTerrainType.editText?.text.toString(),
            roadType = binding.menuRoadType.editText?.text.toString(),
            uri = imageUri
        )
    }

    private fun returnToDetailedView() {
        activity?.onBackPressed()
    }

    private fun setupFormChoices() {
        binding.contentBoardType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.menu_choice_item,
                resources.getStringArray(R.array.board_choices)
            )
        )

        binding.contentTerrainType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.menu_choice_item,
                resources.getStringArray(R.array.hill_choices)
            )
        )

        binding.contentRoadType.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.menu_choice_item,
                resources.getStringArray(R.array.road_choices)
            )
        )

    }
}