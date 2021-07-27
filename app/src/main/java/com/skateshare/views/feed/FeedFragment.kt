package com.skateshare.views.feed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.skateshare.R
import com.skateshare.databinding.FragmentFeedBinding
import com.skateshare.viewmodels.FeedViewModel
import com.skateshare.views.feed.PostAdapter
import java.util.*

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding get() = _binding!!
    private lateinit var viewModel: FeedViewModel
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        recyclerView = binding.postList

        adapter = PostAdapter(SleepNightListener { uid ->
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(uid))
        })
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        viewModel.numNewPosts.observe(viewLifecycleOwner, Observer { newCount ->
            newCount?.let {
                if (newCount != 0) {
                    val oldCount = adapter.itemCount
                    adapter.data = viewModel.postsTotal
                    adapter.notifyItemRangeChanged(oldCount, newCount)
                }
            }
            loadUi()
        })

        awaitScrollRequest()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun awaitScrollRequest() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                    && !viewModel.isLoadingData) {
                    viewModel.fetchPosts()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.feed_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun loadUi() {
        binding.postsLoading.visibility = View.GONE
        binding.postList.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}