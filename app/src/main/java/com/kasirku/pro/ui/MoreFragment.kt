package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kasirku.pro.R
import com.kasirku.pro.databinding.FragmentMoreBinding
import com.kasirku.pro.util.LicenseManager
import com.kasirku.pro.util.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        binding.tvProfileName.text = sessionManager.getUserName().ifEmpty { "Boss" }
        binding.tvProfileRole.text = sessionManager.getRoleName()

        showLicenseInfo()

        binding.cardItems.visibility = if (sessionManager.canAccessItemManagement()) View.VISIBLE else View.GONE
        binding.cardReport.visibility = if (sessionManager.canAccessReport()) View.VISIBLE else View.GONE
        binding.cardPurchase.visibility = if (sessionManager.canAccessPurchase()) View.VISIBLE else View.GONE
        binding.cardDelivery.visibility = if (sessionManager.canAccessDelivery()) View.VISIBLE else View.GONE
        binding.cardCustomers.visibility = if (sessionManager.canAccessCustomer()) View.VISIBLE else View.GONE
        binding.cardEmployees.visibility = if (sessionManager.canAccessEmployee()) View.VISIBLE else View.GONE
        binding.cardRoles.visibility = if (sessionManager.canManageRoles()) View.VISIBLE else View.GONE
        binding.cardPriceCheck.visibility = if (sessionManager.canAccessItemManagement()) View.VISIBLE else View.GONE

        binding.cardItems.setOnClickListener { findNavController().navigate(R.id.nav_items) }
        binding.cardReport.setOnClickListener { findNavController().navigate(R.id.reportFragment) }
        binding.cardDelivery.setOnClickListener { findNavController().navigate(R.id.deliveryFragment) }
        binding.cardEmployees.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
        binding.cardRoles.setOnClickListener { findNavController().navigate(R.id.roleManagementFragment) }
        binding.cardSettings.setOnClickListener { findNavController().navigate(R.id.settingsFragment) }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            sessionManager.clearSession()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun showLicenseInfo() {
        val expiresAt = LicenseManager.getExpiresAt(requireContext())
        val licenseKey = LicenseManager.getLicenseKey(requireContext())

        if (licenseKey.isNotEmpty()) {
            binding.tvLicenseInfo.visibility = View.VISIBLE
            if (expiresAt > 0) {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val expiryDate = dateFormat.format(Date(expiresAt))
                val now = System.currentTimeMillis()
                val daysLeft = ((expiresAt - now) / (24 * 60 * 60 * 1000)).toInt()
                binding.tvLicenseInfo.text = if (daysLeft > 0) {
                    "License: $licenseKey\nAktif sampai: $expiryDate ($daysLeft hari lagi)"
                } else {
                    "License: $licenseKey\nMasa aktif habis: $expiryDate"
                }
            } else {
                binding.tvLicenseInfo.text = "License: $licenseKey\nMasa aktif: Permanent"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
