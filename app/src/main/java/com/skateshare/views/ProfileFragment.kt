package com.skateshare.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.R
import com.skateshare.databinding.FragmentProfileBinding
import com.skateshare.viewmodels.ProfileViewModel
import com.skateshare.viewmodels.ProfileViewModelFactory

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var factory: ProfileViewModelFactory
    private var profileUserIsCurrentUser = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val profileUid = ProfileFragmentArgs.fromBundle(requireArguments()).profileUid
        profileUserIsCurrentUser = profileUid == null
        factory = ProfileViewModelFactory(profileUid)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.user.observe(viewLifecycleOwner, { userData ->
            Glide.with(this)
                .load(userData.profilePicture)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.progress.visibility = View.GONE
                        return false
                    }
                }).into(binding.profilePicture)
        })

        // TODO: MOVE ME TO SETTINGS
        binding.logout.setOnClickListener {
            requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedIn", false).apply()
            FirebaseAuth.getInstance().signOut()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (profileUserIsCurrentUser)
            inflater.inflate(R.menu.profile_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}