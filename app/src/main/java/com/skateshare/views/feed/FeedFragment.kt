package com.skateshare.views.feed

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import com.skateshare.viewmodels.feed.FeedViewModel
import com.skateshare.views.feed.feedrecyclerview.FeedAdapter
import com.skateshare.views.feed.feedrecyclerview.FeedItemListener

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
        viewModel.fetchPosts()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        _recyclerView = binding.postList

        getDataFromSharedPreferences()

        _adapter = FeedAdapter(FeedItemListener({ uid ->
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

        viewModel.numNewPosts.observe(viewLifecycleOwner, { num ->
            if (num >= 0) {
                binding.refreshLayout.isRefreshing = false
                adapter.submitList(viewModel.getData())
                if (!uiIsInitialized)
                    loadUi()
                viewModel.resetNumNewPosts()
            }
        })

        viewModel.deleteResponse.observe(viewLifecycleOwner, { response ->
            response?.let {
                if (response.isEnabled) {
                    if (response.isSuccessful) {
                        adapter.submitList(viewModel.getData())
                        Snackbar.make(requireView(), R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
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

        unit = sharedPreferences.getString("units", UNIT_MILES) ?: UNIT_MILES
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
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = layoutManager.findLastCompletelyVisibleItemPosition()
                if (dy > 0
                    && position == adapter.itemCount - 1
                    && !viewModel.isLoadingData) {
                    recyclerView.post {
                        viewModel.fetchPosts()
                    }
                }
            }
        })
    }

    private fun loadUi() {
        binding.postsLoading.visibility = View.GONE
        binding.postList.visibility = View.VISIBLE
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