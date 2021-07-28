package com.skateshare.views.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skateshare.R
import com.skateshare.databinding.FragmentSettingsBinding
import com.skateshare.viewmodels.SettingsViewModel
import com.skateshare.views.authentication.AuthenticationActivity


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        binding.logout.setOnClickListener {
            updatePreferences()
            viewModel.logout()
            goToLogin()
        }

        binding.deleteAccount.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account?")
                .setMessage("This action is irreversible.\nAll public and private routes will be deleted.")
                .setPositiveButton("Delete") {_,_ ->
                    updatePreferences()
                    viewModel.deleteAccount()
                    goToLogin()
                }
                .setNegativeButton("Cancel") {_,_-> /* Alert dismissed */ }
                .show()
        }

        return binding.root
    }

    private fun goToLogin() {
        startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
        requireActivity().finish()
    }

    private fun updatePreferences() {
        requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
            .putBoolean("isLoggedIn", false).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}