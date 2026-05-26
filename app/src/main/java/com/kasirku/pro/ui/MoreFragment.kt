package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kasirku.pro.R
import com.kasirku.pro.databinding.FragmentMoreBinding
import com.kasirku.pro.util.SessionManager

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

        binding.cardReport.visibility = if (sessionManager.canAccessReport()) View.VISIBLE else View.GONE
        binding.cardPurchase.visibility = if (sessionManager.canAccessPurchase()) View.VISIBLE else View.GONE
        binding.cardDelivery.visibility = if (sessionManager.canAccessDelivery()) View.VISIBLE else View.GONE
        binding.cardCustomers.visibility = if (sessionManager.canAccessCustomer()) View.VISIBLE else View.GONE
        binding.cardEmployees.visibility = if (sessionManager.canAccessEmployee()) View.VISIBLE else View.GONE
        binding.cardRoles.visibility = if (sessionManager.canManageRoles()) View.VISIBLE else View.GONE
        binding.cardSettings.visibility = if (sessionManager.canAccessSettings()) View.VISIBLE else View.GONE
        binding.cardPriceCheck.visibility = if (sessionManager.canAccessItemManagement()) View.VISIBLE else View.GONE

        binding.cardReport.setOnClickListener { findNavController().navigate(R.id.reportFragment) }
        binding.cardDelivery.setOnClickListener { findNavController().navigate(R.id.deliveryFragment) }
        binding.cardEmployees.setOnClickListener { findNavController().navigate(R.id.registerFragment) }
        binding.cardRoles.setOnClickListener { findNavController().navigate(R.id.roleManagementFragment) }
        binding.cardSettings.setOnClickListener { findNavController().navigate(R.id.settingsFragment) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
