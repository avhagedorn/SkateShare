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
import androidx.navigation.fragment.findNavController
import com.skateshare.R
import com.skateshare.databinding.FragmentLoginBinding
import com.skateshare.viewmodels.AuthViewModel
import com.skateshare.viewmodels.AuthViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels(
        factoryProducer = {
            AuthViewModelFactory(requireContext().getSharedPreferences(
                "userData", Context.MODE_PRIVATE)) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.submitLogin.setOnClickListener {
            viewModel.login(
                email = binding.emailInput.text.toString(),
                password = binding.passwordInput.text.toString(),
                context = context)
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.registerHint.setOnClickListener { goToAuthentication() }

        viewModel.loginResponse.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                viewModel.updateLoginStatus(true)
                goToMainActivity()
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), response.exception!!.message.toString(), Toast.LENGTH_LONG).show()
            }
        })

        return binding.root
    }

    private fun goToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun goToAuthentication() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}