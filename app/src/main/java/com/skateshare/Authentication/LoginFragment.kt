package com.skateshare.Authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.MainActivity
import com.skateshare.R
import com.skateshare.databinding.FragmentLoginBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        val view = binding.root

        // Register redirect
        binding.registerHint.setOnClickListener { goToAuthentication() }

        // Form submitted
        binding.submitLogin.setOnClickListener {
            when {

                // Empty username
                binding.emailInput.text.toString().trim{it<=' '}.isEmpty() -> {
                    Toast.makeText(context, R.string.missing_email, Toast.LENGTH_SHORT).show()
                }

                // Empty password
                binding.passwordInput.text.toString().trim{it<=' '}.isEmpty() -> {
                    Toast.makeText(context, R.string.missing_password, Toast.LENGTH_SHORT).show()
                }

                // Short password
                binding.passwordInput.text.toString().length < 8 -> {
                    Toast.makeText(context, R.string.short_password, Toast.LENGTH_LONG).show()
                }
                else -> {
                    val email = binding.emailInput.text.toString()
                    val password = binding.passwordInput.text.toString()

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {
                                    updateLoginStatus()
                                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                                    requireActivity().finish()

                                } else {
                                    Toast.makeText(
                                        context,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                }

                // To logout with Firebase:
                // FirebaseAuth.getInstance().signOut()

            }
        }
        return view
    }

    private fun goToAuthentication() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(action)
    }

    // TODO: Combine with registerfragment function
    private fun updateLoginStatus() {
        requireActivity().getSharedPreferences("userData", Context.MODE_PRIVATE)
            .edit().putBoolean("isLoggedIn", true).apply()
    }
}