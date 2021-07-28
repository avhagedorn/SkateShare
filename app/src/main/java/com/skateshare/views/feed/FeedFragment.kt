package com.skateshare.views.feed

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentFeedBinding
import com.skateshare.viewmodels.FeedViewModel
import com.skateshare.views.feed.recyclerviewcomponents.PostAdapter
import com.skateshare.views.feed.recyclerviewcomponents.SleepNightListener
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

        adapter = PostAdapter(SleepNightListener({ uid ->
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(uid))
        }, { postId ->
            viewModel.deletePost(postId)
        }))
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter
        adapter.data = viewModel.postsTotal

        viewModel.numNewPosts.observe(viewLifecycleOwner, Observer { newCount ->
            newCount?.let {
                if (newCount != 0)
                    displayNewPosts(newCount)
                else
                    // Empty response implies no more elements, so notify loading icon is removed
                    adapter.notifyItemRemoved(adapter.itemCount)
            }
            loadUi()
        })

        viewModel.dbResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response == null)
                Snackbar.make(requireView(), R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
            else
                Snackbar.make(requireView(), response, Snackbar.LENGTH_LONG).show()
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
                // adapter.itemCount - 2 enables the spinner to exist before the user
                // reaches the bottom, resulting in a slightly smoother experience.
                if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 2
                    && !viewModel.isLoadingData) {
                    recyclerView.post {
                        adapter.data.add(null)
                        adapter.notifyItemInserted(adapter.itemCount-1)
                    }
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

    private fun displayNewPosts(newCount: Int) {
        val oldCount = adapter.itemCount - newCount
        adapter.notifyItemRangeChanged(oldCount, newCount)
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