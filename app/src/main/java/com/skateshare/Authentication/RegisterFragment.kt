package com.skateshare.Authentication

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
import com.google.firebase.auth.FirebaseUser
import com.skateshare.R
import com.skateshare.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        val view = binding.root

        // Login redirect
        binding.registerHint.setOnClickListener { goToLogin() }

        // Form submitted
        binding.submitRegistration.setOnClickListener {
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

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                if (task.isSuccessful) {

                                    // TODO: Pass this data to another activity
                                    val user: FirebaseUser = task.result!!.user!!
                                    val userID = user.uid

                                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()

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
            }
        }

        return view
    }

    private fun goToLogin() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(action)
    }
}