package com.skateshare.views.feed

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    private var _adapter: PostAdapter? = null
    private val adapter: PostAdapter get() = _adapter!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        _recyclerView = binding.postList

        _adapter = PostAdapter(SleepNightListener({ uid ->
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(uid))
        }, { postId, position ->
            confirmDeleteModal(postId, position)
        }))
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        binding.refreshLayout.setOnRefreshListener { refresh() }

        viewModel.numNewPosts.observe(viewLifecycleOwner, Observer { newCount ->
            newCount?.let {
                val lastIndex = adapter.itemCount - 1
                if (adapter.data.isNotEmpty()) {
                    // Remove null flag
                    adapter.data.removeAt(lastIndex)
                    adapter.notifyItemRemoved(lastIndex)
                }
                if (newCount != 0) {
                    adapter.data.addAll(viewModel.currentPosts)
                    adapter.notifyItemRangeInserted(lastIndex+1, newCount)
                }
                loadUi()
            }
        })

        viewModel.dbResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                if (response.message == null) {
                    val position = response.viewIndex
                    Snackbar.make(requireView(), R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
                    adapter.data.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    // Updates underlying indices in case of multiple deletions
                    adapter.notifyItemRangeChanged(position, adapter.itemCount - position)
                }
                else
                    Snackbar.make(requireView(), response.message, Snackbar.LENGTH_LONG).show()
            }
        })

        awaitScrollRequest()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun refresh() {
        hideUi()
        adapter.clear()
        viewModel.refreshData()
        recyclerView.smoothScrollToPosition(0)
//        adapter.data = viewModel.postsTotal
        binding.refreshLayout.isRefreshing = false
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
                if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 2
                    && !viewModel.isLoadingData) {
                    recyclerView.post {
                        val dataSize = adapter.itemCount
                        adapter.data.add(null)
                        adapter.notifyItemInserted(dataSize)
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
        binding.postList.visibility = View.GONE
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
        Log.i("1one", "Feed Destroyed")
        binding.refreshLayout.setOnRefreshListener(null)
        _adapter = null
        _recyclerView = null
        _binding = null
    }
}