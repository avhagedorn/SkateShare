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
import com.skateshare.misc.DEFAULT_PROFILE_PICTURE_URL
import com.skateshare.viewmodels.authentication.AuthViewModel
import com.skateshare.views.MainActivity

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
            val username = binding.usernameInput.text.toString()
            binding.progressBar.visibility = View.VISIBLE
            viewModel.register(
                email=binding.emailInput.text.toString(),
                password=binding.passwordInput.text.toString(),
                username=username,
                createNewUserData(username))
        }

        viewModel.checkCredentialsEmpty.observe(viewLifecycleOwner, { event ->
            if (!event.success) {
                displayError(getString(event.response))
                viewModel.resetCredentialError()
            }
        })

        viewModel.loginResponse.observe(viewLifecycleOwner) { response ->
            if (response.isEnabled) {
                if (response.isSuccessful) {
                    saveLoginStatus()
                    goToMainActivity()
                } else {
                    displayError(response.message!!)
                    viewModel.resetLoginException()
                }
            }
        }

        return binding.root
    }

    private fun createNewUserData(username: String) : HashMap<String, Any?> =
        hashMapOf(
            "username" to username,
            "name" to "",
            "bio" to getString(R.string.default_bio),
            "profilePicture" to DEFAULT_PROFILE_PICTURE_URL
        )

    private fun goToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
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