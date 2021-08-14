package com.skateshare.views.routes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentPrivateRoutesBinding
import com.skateshare.misc.UNIT_MILES
import com.skateshare.viewmodels.PrivateRoutesViewModel
import com.skateshare.views.routes.recyclerviewcomponents.RouteListener
import com.skateshare.views.routes.recyclerviewcomponents.RoutesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivateRoutesFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentPrivateRoutesBinding? = null
    private val binding: FragmentPrivateRoutesBinding get() = _binding!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!
    private var _adapter: RoutesAdapter? = null
    private val adapter: RoutesAdapter get() = _adapter!!
    private lateinit var unit: String
    private lateinit var viewModel: PrivateRoutesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_private_routes, container, false)
        viewModel = ViewModelProvider(this).get(PrivateRoutesViewModel::class.java)
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        binding.sortOptions.onItemSelectedListener = this
        _recyclerView = binding.postList
        unit = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
            .getString("units", UNIT_MILES)!!

        _adapter = RoutesAdapter(RouteListener ({ routeId ->
            goToDetailedView(routeId)
        }, { index, route ->
            viewModel.deleteRoute(index, route)
        }), unit)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        binding.refreshLayout.setOnRefreshListener { refreshData() }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sorting_choices,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortOptions.adapter = adapter
        }

        viewModel.numNewRoutes.observe(viewLifecycleOwner, { numRoutes ->
            if (numRoutes >= 0) {
                adapter.submitList(viewModel.getData())
                loadUi()
                viewModel.resetNumNewRoutes()
            }
        })

        viewModel.deleteResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled) {
                if (response.isSuccessful) {
                    adapter.submitList(viewModel.getData())
                    Snackbar.make(requireView(),
                        R.string.route_deleted, Snackbar.LENGTH_SHORT).show()
                } else
                     Toast.makeText(requireContext(),
                         response.message, Toast.LENGTH_SHORT).show()
                viewModel.resetDeleteResponse()
            }
        })

        //awaitScrollRequest()
        return binding.root
    }

    private fun awaitScrollRequest() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                // adapter.itemCount - 2 enables the spinner to exist before the user
                // reaches the bottom, resulting in a slightly smoother experience.
                val position = layoutManager.findLastCompletelyVisibleItemPosition()
                if (position == adapter.itemCount - 1
                    && viewModel.allRoutes.size > 1
                    && !viewModel.isLoadingData) {
                    viewModel.getRoutes()
                    recyclerView.post {
                        adapter.submitList(viewModel.getData())
                    }
                }
            }
        })
    }

    private fun goToDetailedView(routeId: Long) {
        findNavController().navigate(
            PrivateRoutesFragmentDirections
                .actionPrivateRoutesFragmentToDetailedPrivateRouteFragment(routeId))
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

    private fun refreshData() {
        binding.refreshLayout.isRefreshing = true
        fetchData()
    }

    private fun fetchData() {
        hideUi()
        viewModel.clearExistingRoutes()
        viewModel.getRoutes()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // position corresponds 1-to-1 with BY_XXXX query codes, so we use it directly here.
        viewModel.updateSortingPreference(position)
        fetchData()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _recyclerView = null
        _adapter = null
    }

}