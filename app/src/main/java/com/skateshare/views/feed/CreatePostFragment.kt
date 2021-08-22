package com.skateshare.views.feed

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.skateshare.R
import com.skateshare.databinding.FragmentCreatePostBinding
import com.skateshare.misc.MAX_POST_IMAGE_SIZE
import com.skateshare.misc.fileSizeMb
import com.skateshare.viewmodels.feed.CreatePostViewModel

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
        val getPostImage = registerForActivityResult(ActivityResultContracts.GetContent()) { nullableUri ->
            nullableUri?.let { uri ->
                if (uri.fileSizeMb(requireContext()) < MAX_POST_IMAGE_SIZE) {
                    Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(binding.postImage)
                    updatedUri = uri
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.image_mb_too_large).format(MAX_POST_IMAGE_SIZE),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.postImage.setOnClickListener { getPostImage.launch("image/*") }
        binding.cancelSubmit.setOnClickListener { returnToFeed() }
        binding.submitPost.setOnClickListener {
            if (updatedUri != null) {
                binding.postProgress.visibility = View.VISIBLE
                viewModel.pushPost(
                    uri = updatedUri!!,
                    description = binding.postDescription.text.toString())
            }
            else
                Toast.makeText(requireContext(), R.string.image_required, Toast.LENGTH_SHORT).show()
        }

        viewModel.exceptionResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled) {
                if (response.isSuccessful) {
                    Snackbar.make(requireView(), R.string.post_created, Snackbar.LENGTH_LONG).show()
                    findNavController().navigate(CreatePostFragmentDirections.actionCreatePostFragmentToFeedFragment())
                } else {
                    binding.postProgress.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
                viewModel.resetException()
            }
        })
        return binding.root
    }

    private fun returnToFeed() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}