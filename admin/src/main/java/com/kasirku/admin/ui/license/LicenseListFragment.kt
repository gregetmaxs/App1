package com.kasirku.admin.ui.license

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.admin.R
import com.kasirku.admin.adapter.LicenseAdapter
import com.kasirku.admin.databinding.FragmentLicenseListBinding
import com.kasirku.admin.viewmodel.LicenseViewModel

class LicenseListFragment : Fragment() {

    private var _binding: FragmentLicenseListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LicenseViewModel by activityViewModels()
    private lateinit var adapter: LicenseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLicenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LicenseAdapter(
            onToggle = { license ->
                viewModel.toggleLicense(license)
            }
        )

        binding.rvLicenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@LicenseListFragment.adapter
        }

        viewModel.allLicenses.observe(viewLifecycleOwner) { licenses ->
            adapter.submitList(licenses)
            binding.tvEmpty.visibility = if (licenses.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.toggleResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                val status = if (it.isActive) "diaktifkan" else "dinonaktifkan"
                Toast.makeText(requireContext(), "License $status", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGenerate.setOnClickListener {
            findNavController().navigate(R.id.action_licenses_to_generate)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    viewModel.allLicenses.observe(viewLifecycleOwner) { adapter.submitList(it) }
                } else {
                    viewModel.searchLicenses("%$query%").observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
