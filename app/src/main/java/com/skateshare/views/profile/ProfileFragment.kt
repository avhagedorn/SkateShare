package com.skateshare.views.profile

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
import com.skateshare.R
import com.skateshare.databinding.FragmentProfileBinding
import com.skateshare.viewmodels.ProfileViewModel
import com.skateshare.viewmodels.ProfileViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var factory: ProfileViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val profileUid = ProfileFragmentArgs.fromBundle(requireArguments()).profileUid
        factory = ProfileViewModelFactory(profileUid)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.user.observe(viewLifecycleOwner, { userData ->
            Glide.with(this)
                .load(userData.profilePicture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .circleCrop()
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                        showProfile()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        showProfile()
                        return false
                    }
                }).into(binding.profilePicture)
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.profileUserIsCurrentUser)
            inflater.inflate(R.menu.profile_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun showProfile() {
        val profileComponents = listOf(binding.bio, binding.userIdentifiers, binding.profilePicture)
        for (component in profileComponents)
            component.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}