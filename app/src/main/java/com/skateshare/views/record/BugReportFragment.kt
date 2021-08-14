package com.skateshare.views.record

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skateshare.R
import com.skateshare.databinding.FragmentBugReportBinding
import com.skateshare.viewmodels.BugReportViewModel

class BugReportFragment : Fragment() {

    private var _binding: FragmentBugReportBinding? = null
    private val binding: FragmentBugReportBinding get() = _binding!!
    private lateinit var viewModel: BugReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bug_report, container, false)

        viewModel = ViewModelProvider(this).get(BugReportViewModel::class.java)

        var updatedUri: Uri? = null
        val getBugScreenshot = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.bugScreenshot)
                updatedUri = uri
            }
        }

        binding.bugScreenshot.setOnClickListener { getBugScreenshot.launch("image/*") }
        binding.submitButton.setOnClickListener { sendBugReport(updatedUri) }
        binding.cancelButton.setOnClickListener { returnToRecord() }

        viewModel.reportResponse.observe(viewLifecycleOwner, { response ->
            if (response.isEnabled) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.bug_report_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    if (response.message == null)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.bug_report_missing_field),
                            Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(
                            requireContext(),
                            response.message,
                            Toast.LENGTH_SHORT).show()
                        returnToRecord()
                    }
                }
                viewModel.resetReportResponse()
            }
        })

        return binding.root
    }

    private fun returnToRecord() {
        activity?.onBackPressed()
    }

    private fun sendBugReport(uri: Uri?) {
        val bugLocation = binding.bugLocation.text.toString()
        val bugDescription = binding.bugDescription.text.toString()
        viewModel.submitBugReport(bugLocation, bugDescription, uri)
    }
}