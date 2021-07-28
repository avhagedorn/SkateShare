package com.skateshare.views.feed

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentCreatePostBinding
import com.skateshare.viewmodels.CreatePostViewModel

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    val binding: FragmentCreatePostBinding get() = _binding!!
    private lateinit var viewModel: CreatePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_post, container, false)
        viewModel = ViewModelProvider(this).get(CreatePostViewModel::class.java)

        var updatedUri: Uri? = null
        val getProfileImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.postImage)
                updatedUri = uri
            }
        }

        binding.postImage.setOnClickListener { getProfileImage.launch("image/*") }

        binding.cancelSubmit.setOnClickListener { returnToFeed() }

        binding.submitPost.setOnClickListener {
            if (updatedUri != null) {
                viewModel.pushPost(
                    uri = updatedUri!!,
                    description = binding.postDescription.text.toString())
            }
            else
                Toast.makeText(requireContext(), R.string.image_required, Toast.LENGTH_SHORT).show()
        }

        viewModel.exceptionResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                if (response.success) {
                    Snackbar.make(requireView(), R.string.post_created, Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
                } else
                    Snackbar.make(requireView(), response.status!!, Snackbar.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun returnToFeed() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("CreatePostFragment", "View Destroyed")
        _binding = null
    }
}