package com.skateshare.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.skateshare.R
import com.skateshare.databinding.FragmentRegisterBinding
import com.skateshare.viewmodels.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.registerHint.setOnClickListener { goToLogin() }

        binding.submitRegistration.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.register(
                email=binding.emailInput.text.toString(),
                password=binding.passwordInput.text.toString(),
                username=binding.usernameInput.text.toString())
        }

        viewModel.checkCredentialsEmpty.observe(viewLifecycleOwner, { event ->
            if (!event.success)
                displayError(getString(event.response))
        })

        viewModel.loginResponse.observe(viewLifecycleOwner, { errorResponse ->
            if (errorResponse == null) {
                saveLoginStatus()
                goToMainActivity()
            } else
                displayError(errorResponse)
        })

        return binding.root
    }

    private fun goToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun goToLogin() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun saveLoginStatus() {
        requireContext().getSharedPreferences("userSettings", Context.MODE_PRIVATE)
            .edit().putBoolean("isLoggedIn", true).apply()
    }

    private fun displayError(error: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}