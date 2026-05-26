package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kasirku.pro.R
import com.kasirku.pro.databinding.FragmentSettingsBinding
import com.kasirku.pro.util.ThemeManager
import com.kasirku.pro.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            settings?.let {
                binding.etStoreName.setText(it.storeName)
                binding.etStoreAddress.setText(it.storeAddress)
                binding.etStorePhone.setText(it.storePhone)
                binding.switchDelivery.isChecked = it.deliveryEnabled
            }
        }

        val currentTheme = ThemeManager.getThemeMode(requireContext())
        when (currentTheme) {
            ThemeManager.THEME_LIGHT -> binding.rbLight.isChecked = true
            ThemeManager.THEME_DARK -> binding.rbDark.isChecked = true
            ThemeManager.THEME_SYSTEM -> binding.rbSystem.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rbLight -> ThemeManager.THEME_LIGHT
                R.id.rbDark -> ThemeManager.THEME_DARK
                R.id.rbSystem -> ThemeManager.THEME_SYSTEM
                else -> ThemeManager.THEME_LIGHT
            }
            ThemeManager.setThemeMode(requireContext(), mode)
        }

        binding.switchDelivery.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDelivery(isChecked)
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etStoreName.text.toString().trim()
            val address = binding.etStoreAddress.text.toString().trim()
            val phone = binding.etStorePhone.text.toString().trim()

            viewModel.updateStoreInfo(name, address, phone)
            Toast.makeText(requireContext(), "Pengaturan tersimpan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
