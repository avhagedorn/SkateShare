package com.skateshare.views.feed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skateshare.R
import com.skateshare.databinding.FragmentFeedBinding
import com.skateshare.views.feed.RecyclerComponents.PostAdapter

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding: FragmentFeedBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)

        val adapter = PostAdapter()
        binding.postList.adapter = adapter
        binding.postList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            posts?.let {
                adapter.submitList(it)
            }
            loadUi()
        })

        return binding.root
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