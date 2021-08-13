package com.skateshare.views.feed

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentFeedBinding
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.LoadingItem
import com.skateshare.viewmodels.FeedViewModel
import com.skateshare.views.feed.recyclerviewcomponents.FeedAdapter
import com.skateshare.views.feed.recyclerviewcomponents.SleepNightListener

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding get() = _binding!!
    private lateinit var viewModel: FeedViewModel
    private lateinit var unit: String
    private var avgSpeed: Float = 0f
    private var _adapter: FeedAdapter? = null
    private val adapter: FeedAdapter get() = _adapter!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!
    var uiIsInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        _recyclerView = binding.postList

        getDataFromSharedPreferences()

        _adapter = FeedAdapter(SleepNightListener({ uid ->
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToProfileFragment(uid))
        }, { postId, position ->
            confirmDeleteModal(postId, position)
        }, { lat, lng ->
            findNavController().navigate(
                FeedFragmentDirections.actionFeedFragmentToRoutesFragment(
                    true,
                    lat,
                    lng
                ))
        }), unit, avgSpeed)

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        binding.refreshLayout.setOnRefreshListener { refresh() }

        viewModel.numNewPosts.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
            adapter.submitList(viewModel.getData())
            if (!uiIsInitialized)
                loadUi()
        })

        viewModel.dbResponse.observe(viewLifecycleOwner, { response ->
            response?.let {
                if (response.enabled) {
                    if (response.message == null) {
                        adapter.submitList(viewModel.getData())
                        Snackbar.make(requireView(), R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
                    }
                    else
                        Snackbar.make(requireView(), response.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetRecyclerItemResponse()
                }
            }
        })

        awaitScrollRequest()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun getDataFromSharedPreferences() {
        val sharedPreferences = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)

        unit = sharedPreferences.getString("units", UNIT_MILES)!!
        avgSpeed = when (unit) {
            UNIT_MILES -> sharedPreferences.getFloat("avgSpeedMi", 0f)
            UNIT_KILOMETERS -> sharedPreferences.getFloat("avgSpeedKm", 0f)
            else -> 0f
        }
    }

    private fun refresh() {
        binding.refreshLayout.isRefreshing = true
        recyclerView.smoothScrollToPosition(0)
        viewModel.refreshData()
    }

    private fun confirmDeleteModal(postId: String, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_delete_post)
            .setMessage(R.string.irreversible)
            .setPositiveButton(R.string.delete) {_,_ -> viewModel.deletePost(postId, position) }
            .setNegativeButton(R.string.cancel) {_,_-> /* Alert dismissed */ }
            .show()
    }

    private fun awaitScrollRequest() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                // adapter.itemCount - 2 enables the spinner to exist before the user
                // reaches the bottom, resulting in a slightly smoother experience.
                val position = layoutManager.findLastCompletelyVisibleItemPosition()
                if ((position == adapter.itemCount - 2 || position == adapter.itemCount - 1)
                    && !viewModel.isLoadingData) {
                    recyclerView.post {
                        val loadingList = viewModel.getData()
                        loadingList.add(LoadingItem())
                        adapter.submitList(loadingList)
                    }
                    viewModel.fetchPosts()
                }
            }
        })
    }

    private fun loadUi() {
        binding.postsLoading.visibility = View.GONE
        binding.postList.visibility = View.VISIBLE
    }

    private fun hideUi() {
        binding.postsLoading.visibility = View.VISIBLE
        binding.postList.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.feed_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.createPostFragment ->
                NavigationUI.onNavDestinationSelected(item, findNavController())
            R.id.refresh_button -> {
                refresh()
                false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.refreshLayout.setOnRefreshListener(null)
        _binding = null
        _recyclerView = null        // LeakCanary leaks unless RecyclerView is manually nulled
        _adapter = null
    }
}