package com.skateshare.views.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skateshare.R
import com.skateshare.databinding.FragmentEditBoardBinding
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.viewmodels.EditBoardViewModel

class EditBoardFragment : Fragment() {

    private var _binding: FragmentEditBoardBinding? = null
    private val binding: FragmentEditBoardBinding get() = _binding!!
    private lateinit var viewModel: EditBoardViewModel
    private var units: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_board, container, false)
        viewModel = ViewModelProvider(this).get(EditBoardViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        setupLayout()

        units = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)
            .getString("units", UNIT_MILES)

        var updatedUri: Uri? = null
        val getProfileImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.boardImage)
                updatedUri = uri
            }
        }

        binding.boardImage.setOnClickListener { getProfileImage.launch("image/*") }
        binding.cancel.setOnClickListener { returnToProfile() }

        return binding.root
    }

    private fun setupLayout() {
        viewModel.board.observe(viewLifecycleOwner, { board ->
            Glide.with(this)
                .load(board.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .centerCrop()
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                        loadUi(false)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        binding.speed.setText (
                            when (units) {
                                UNIT_MILES -> board.topSpeedMph.toString()
                                UNIT_KILOMETERS -> board.topSpeedKph.toString()
                                else -> "Undefined"
                            },
                            TextView.BufferType.EDITABLE
                        )
                        loadUi(true)
                        return false
                    }
                }).into(binding.boardImage)
        })
    }

    private fun loadUi(loadSuccessful: Boolean) {
        if (loadSuccessful)
            for (item in listOf(binding.submitBoard, binding.cancel, binding.scrollView))
                item.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
    }

    private fun returnToProfile() {
        activity?.onBackPressed()
    }
}