package com.skateshare.views.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.skateshare.R
import com.skateshare.databinding.FragmentProfileBinding
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Board
import com.skateshare.viewmodels.ProfileViewModel
import com.skateshare.viewmodels.ProfileViewModelFactory
import com.skateshare.views.feed.feedrecyclerview.FeedAdapter
import com.skateshare.views.feed.feedrecyclerview.FeedItemListener

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel
    private lateinit var factory: ProfileViewModelFactory
    private var uid: String? = null

    private lateinit var unit: String
    private var avgSpeed: Float = 0f
    private var _adapter: FeedAdapter? = null
    private val adapter: FeedAdapter get() = _adapter!!
    private var _recyclerView: RecyclerView? = null
    private val recyclerView: RecyclerView get() = _recyclerView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        uid = ProfileFragmentArgs.fromBundle(requireArguments()).profileUid
        getDataFromSharedPreferences()
        initViewModel()

        binding.units = unit
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.postList.layoutManager = LinearLayoutManager(requireContext())
        _recyclerView = binding.postList

        _adapter = FeedAdapter(FeedItemListener(
            { _ -> },
            { postId, position -> viewModel.deletePost(postId, position) },
            { lat, lng ->
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToRoutesFragment(
                        true,
                        lat,
                        lng
                    ))
            }), unit, avgSpeed)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.postList.adapter = adapter

        awaitScrollRequest()
        setViewModelListeners()
        setTabListener()
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun awaitScrollRequest() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position = layoutManager.findLastVisibleItemPosition()
                if (dy > 0
                    && position == adapter.itemCount - 1
                    && !viewModel.isLoadingData) {
                    recyclerView.post {
                        viewModel.fetchPosts()
                    }
                }
            }
        })
    }

    private fun initViewModel() {
        factory = ProfileViewModelFactory(uid)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)
        viewModel.getBoard()
    }

    private fun getDataFromSharedPreferences() {
        val sharedPreferences = requireContext()
            .getSharedPreferences("userData", Context.MODE_PRIVATE)

        unit = sharedPreferences.getString("units", UNIT_MILES)!!
        avgSpeed = when (unit) {
            UNIT_MILES -> sharedPreferences.getFloat("avgSpeedMi", 0f)
            UNIT_KILOMETERS -> sharedPreferences.getFloat("avgSpeedKm", 0f)
            else -> 0f
        }
    }

    private fun setViewModelListeners() {
        viewModel.user.observe(viewLifecycleOwner, { userData ->
            if (userData == null) {
                binding.progress.visibility = View.GONE
                binding.userNotFound.visibility = View.VISIBLE
            } else
                Glide.with(this)
                    .load(userData.profilePicture)
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

        viewModel.board.observe(viewLifecycleOwner, { board ->
            if (board == null) {
                binding.boardHolder.visibility = View.VISIBLE
                binding.contentLoading.visibility = View.GONE
                binding.noBoard.visibility = View.VISIBLE
            } else {
                Glide.with(this)
                    .load(board.imageUrl)
                    .centerCrop()
                    .listener(object: RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Drawable>?,
                            isFirstResource: Boolean): Boolean {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                            binding.contentLoading.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?, model: Any?, target: Target<Drawable>?,
                            dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            showBoard(board)
                            return false
                        }
                    })
                    .into(binding.boardImage)
            }
        })

        viewModel.numNewPosts.observe(viewLifecycleOwner, { numNewPosts ->
            if (numNewPosts >= 0) {
                adapter.submitList(viewModel.getData())
                viewModel.resetNumNewPosts()
                showPosts()
            }
        })

        viewModel.deleteResponse.observe(viewLifecycleOwner, { response ->
            response?.let {
                if (response.isEnabled) {
                    if (response.isSuccessful) {
                        adapter.submitList(viewModel.getData())
                        Snackbar.make(requireView(), R.string.post_deleted, Snackbar.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetRecyclerItemResponse()
                }
            }
        })
    }

    private fun showProfile() {
        binding.content.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
    }

    private fun showBoard(board: Board) {
        binding.description.text = board.description
        binding.boardTags.text = setTags(
            getString(R.string.board_tags),
            getString(R.string.speed_mph),
            getString(R.string.speed_kph),
            board,
            unit)
        binding.contentLoading.visibility = View.GONE
        binding.boardHolder.visibility = View.VISIBLE
    }

    private fun loadPosts() {
        if (viewModel.totalItems.size == 0) {
            binding.contentLoading.visibility = View.VISIBLE
            viewModel.fetchPosts()
        } else
            showPosts()
    }

    private fun showPosts() {
        binding.contentLoading.visibility = View.GONE
        binding.postList.visibility = View.VISIBLE
    }

    private fun setTabListener() {
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when(it.position) {
                        0 -> binding.boardHolder.visibility = View.VISIBLE
                        1 -> loadPosts()
                        else -> throw Exception("Invalid tab index!")
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    when(it.position) {
                        0 -> binding.boardHolder.visibility = View.GONE
                        1 -> binding.postList.visibility = View.GONE
                        else -> throw Exception("Invalid tab index!")
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _recyclerView = null
        _adapter = null
    }
}