package com.skateshare.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import coil.load
import com.skateshare.R
import com.skateshare.databinding.FragmentProfileBinding
import com.skateshare.viewmodels.ProfileViewModel
import com.skateshare.viewmodels.ProfileViewModelFactory

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels(
        factoryProducer = { ProfileViewModelFactory() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        binding.logout.setOnClickListener {
            requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", false).apply()
        }

        viewModel.user.observe(viewLifecycleOwner, { userData ->
            binding.username.text = userData.username
            Log.d("ProfileFragment", userData.username)
            binding.realName.text = userData.name
            binding.bio.text = userData.bio
            binding.profilePicture.load(userData.profilePicture)
        })

        return binding.root
    }
}