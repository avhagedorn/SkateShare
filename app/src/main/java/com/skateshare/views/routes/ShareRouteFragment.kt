package com.skateshare.views.routes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.skateshare.R
import com.skateshare.databinding.FragmentShareRouteBinding
import com.skateshare.misc.*
import com.skateshare.viewmodels.ShareRouteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareRouteFragment : Fragment() {

    private var _binding: FragmentShareRouteBinding? = null
    private val binding: FragmentShareRouteBinding get() = _binding!!
    private lateinit var viewModel: ShareRouteViewModel
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
            if (response.status != null) {
                binding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), response.status, Toast.LENGTH_SHORT).show()
                if (response.success)
                    returnToDetailedView()
                viewModel.resetPostResponse()
            }
        })

        setupFormChoices()
        return binding.root
    }

    private fun submitPost() {
        binding.loading.visibility = View.VISIBLE
        viewModel.submitPost(
            routeId = routeId,
            routeDescription = binding.description.text.toString(),
            boardType = binding.menuBoardType.editText?.text.toString(),
            terrainType = binding.menuTerrainType.editText?.text.toString(),
            roadType = binding.menuRoadType.editText?.text.toString()
        )
    }

    private fun returnToDetailedView() {
        activity?.onBackPressed()
    }

    private fun setupFormChoices() {
        binding.contentBoardType.setAdapter(
            ArrayAdapter(requireContext(), R.layout.menu_choice_item,
                listOf(SHORTBOARD, LONGBOARD, MOUNTAINBOARD)))

        binding.contentTerrainType.setAdapter(
            ArrayAdapter(requireContext(), R.layout.menu_choice_item,
                listOf(LOW_HILLS, MEDIUM_HILLS, HIGH_HILLS))
        )

        binding.contentRoadType.setAdapter(
            ArrayAdapter(requireContext(), R.layout.menu_choice_item,
                listOf(SMOOTH_ROADS, AVERAGE_ROADS, ROUGH_ROADS, NO_ROADS))
        )
    }
}