package com.skateshare.views

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skateshare.R
import com.skateshare.databinding.FragmentEditProfileBinding
import com.skateshare.viewmodels.EditProfileViewModel
import java.io.File
import java.net.URI

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
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.profilePicture)
                updatedUri = uri
            }
        }

        binding.profilePicture.setOnClickListener { getProfileImage.launch("image/*") }

        binding.cancelEdit.setOnClickListener { returnToProfile() }

        binding.confirmEdit.setOnClickListener {
            viewModel.updateProfile(hashMapOf(
                    "username" to binding.editUsername.text.toString(),
                    "name" to binding.editName.text.toString(),
                    "bio" to binding.editBio.text.toString()
                ))
            if (updatedUri != null)
                viewModel.uploadProfilePicture(updatedUri!!)
            binding.progressBar.visibility = View.VISIBLE
        }

        viewModel.response.observe(viewLifecycleOwner) { response ->
            binding.progressBar.visibility = View.GONE
            if (response == null) {
                sendToast(getString(R.string.edit_success))
                returnToProfile()
            } else
                sendToast(response)
        }

        return binding.root
    }

    private fun sendToast(alert: String) {
        Toast.makeText(requireContext(), alert, Toast.LENGTH_LONG).show()
    }

    private fun setupLayout() {
        viewModel.user.observe(viewLifecycleOwner, { userData ->
            Glide.with(this)
                .load(userData.profilePicture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.profilePicture)
            for (item in listOf(binding.profilePicture, binding.cancelEdit,
                                binding.confirmEdit, binding.editLayout))
                item.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        })
    }

    private fun returnToProfile() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}