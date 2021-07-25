package com.skateshare.views.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skateshare.R
import com.skateshare.databinding.FragmentRegisterBinding
import com.skateshare.viewmodels.AuthViewModel
import com.skateshare.views.profile.ProfileActivity

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        binding.registerHint.setOnClickListener { goToLogin() }

        binding.submitRegistration.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.register(
                email=binding.emailInput.text.toString(),
                password=binding.passwordInput.text.toString(),
                username=binding.usernameInput.text.toString())
        }

        viewModel.checkCredentialsEmpty.observe(viewLifecycleOwner, { event ->
            if (!event.success) {
                displayError(getString(event.response))
                viewModel.resetCredentialError()
            }
        })

        viewModel.loginException.observe(viewLifecycleOwner) { result ->
            if (result.success) {
                if (result.status == null) {
                    saveLoginStatus()
                    goToMainActivity()
                } else {
                    displayError(result.status)
                    viewModel.resetLoginException()
                }
            }
        }

        return binding.root
    }

    private fun goToMainActivity() {
        startActivity(Intent(requireActivity(), ProfileActivity::class.java))
        requireActivity().finish()
    }

    private fun goToLogin() {
        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }

    private fun saveLoginStatus() {
        requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
            .putBoolean("isLoggedIn", true).apply()
    }

    private fun displayError(error: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}