package com.kasirku.admin.ui.license

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kasirku.admin.R
import com.kasirku.admin.databinding.FragmentGenerateLicenseBinding
import com.kasirku.admin.viewmodel.LicenseViewModel

class GenerateLicenseFragment : Fragment() {

    private var _binding: FragmentGenerateLicenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LicenseViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenerateLicenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGenerate.setOnClickListener {
            val storeName = binding.etStoreName.text.toString().trim()
            val ownerName = binding.etOwnerName.text.toString().trim()
            val ownerEmail = binding.etOwnerEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val phone = binding.etOwnerPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val priceStr = binding.etPrice.text.toString().trim()

            if (storeName.isEmpty() || ownerName.isEmpty() || ownerEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Isi semua field yang wajib", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0

            val durationDays = when (binding.rgDuration.checkedRadioButtonId) {
                R.id.rb7Days -> 7L
                R.id.rb14Days -> 14L
                R.id.rb30Days -> 30L
                R.id.rb365Days -> 365L
                R.id.rb730Days -> 730L
                R.id.rbPermanent -> -1L
                else -> -1L
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.btnGenerate.isEnabled = false

            viewModel.generateLicense(storeName, ownerName, ownerEmail, phone, password, price, address, durationDays)
        }

        viewModel.generateResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnGenerate.isEnabled = true

            result.onSuccess { license ->
                binding.cardResult.visibility = View.VISIBLE
                binding.tvGeneratedKey.text = license.licenseKey
                Toast.makeText(requireContext(), "License berhasil dibuat!", Toast.LENGTH_SHORT).show()
            }

            result.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
