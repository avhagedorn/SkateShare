package com.skateshare.views

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skateshare.R
import com.skateshare.databinding.FragmentEditProfileBinding
import com.skateshare.viewmodels.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setup UI
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupLayout()

        var updatedUri: Uri? = null
        val getProfileImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.profilePicture)
                updatedUri = uri
            }
        }

        binding.profilePicture.setOnClickListener { getProfileImage.launch("image/*") }

        binding.cancelEdit.setOnClickListener { returnToProfile() }

        binding.confirmEdit.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.updateProfile(hashMapOf(
                    "username" to binding.editUsername.text.toString(),
                    "name" to binding.editName.text.toString(),
                    "bio" to binding.editBio.text.toString().replace("\n", " ")
                ))
            if (updatedUri != null)
                viewModel.uploadProfilePicture(updatedUri!!)
        }

        viewModel.response.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                sendToast(getString(R.string.edit_success))
                returnToProfile()
            } else
                sendToast(response)
            binding.progressBar.visibility = View.GONE
        }

        return binding.root
    }

    private fun setupLayout() {
        viewModel.user.observe(viewLifecycleOwner, { userData ->
            Glide.with(this)
                .load(userData.profilePicture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .circleCrop()
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                        target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        loadUi(false)
                        sendToast(e.toString())
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        loadUi(true)
                        return false
                    }
                }).into(binding.profilePicture)
        })
    }

    private fun loadUi(loadSuccessful : Boolean) {
        binding.progressBar.visibility = View.GONE
        if (loadSuccessful)
            for (item in listOf(binding.profilePicture, binding.cancelEdit,
                binding.confirmEdit, binding.editLayout))
                item.visibility = View.VISIBLE
    }

    private fun sendToast(alert: String) {
        Toast.makeText(requireContext(), alert, Toast.LENGTH_LONG).show()
    }

    private fun returnToProfile() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("EditProfileFragment", "Destroyed")
        _binding = null
    }
}