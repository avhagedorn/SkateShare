package com.skateshare.views

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skateshare.R
import com.skateshare.databinding.FragmentEditProfileBinding
import com.skateshare.databinding.FragmentLoginBinding
import com.skateshare.databinding.FragmentProfileBinding
import com.skateshare.viewmodels.EditProfileViewModel

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.user.observe(viewLifecycleOwner, { userData ->
            Glide.with(this)
                .load(userData.profilePicture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .circleCrop()
                .into(binding.profilePicture)
        })

        binding.cancelEdit.setOnClickListener { returnToProfile() }

        binding.confirmEdit.setOnClickListener {
            viewModel.updateProfile(hashMapOf<String, Any?>(
                "username" to binding.editUsername.text.toString(),
                "name" to binding.editName.text.toString(),
                "bio" to binding.editBio.text.toString(),
                "profilePicture" to viewModel.updatedProfilePicture))
            returnToProfile()
        }
        return binding.root
    }

    private fun returnToProfile() {
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("EditProfileFragment", "View Destroyed")
        _binding = null
    }
}