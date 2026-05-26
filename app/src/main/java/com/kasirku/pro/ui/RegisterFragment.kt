package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kasirku.pro.data.entity.Role
import com.kasirku.pro.databinding.FragmentRegisterBinding
import com.kasirku.pro.viewmodel.AuthViewModel
import com.kasirku.pro.viewmodel.RoleViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private val roleViewModel: RoleViewModel by viewModels()
    private var roles: List<Role> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roleViewModel.allRoles.observe(viewLifecycleOwner) { roleList ->
            roles = roleList
            val names = roleList.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            binding.spinnerRole.adapter = adapter
        }

        binding.btnRegister.setOnClickListener { registerUser() }

        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.isEnabled = true

            result.onSuccess {
                Toast.makeText(requireContext(), "Karyawan berhasil didaftarkan!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            result.onFailure { e ->
                binding.tvError.text = e.message
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val ktp = binding.etKtp.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || ktp.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
            binding.tvError.text = "Semua field wajib diisi"
            binding.tvError.visibility = View.VISIBLE
            return
        }

        if (ktp.length != 16) {
            binding.tvError.text = "No. KTP harus 16 digit"
            binding.tvError.visibility = View.VISIBLE
            return
        }

        val selectedRoleIndex = binding.spinnerRole.selectedItemPosition
        val selectedRole = if (selectedRoleIndex >= 0 && selectedRoleIndex < roles.size) roles[selectedRoleIndex] else null

        if (selectedRole == null) {
            binding.tvError.text = "Pilih role terlebih dahulu"
            binding.tvError.visibility = View.VISIBLE
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.tvError.visibility = View.GONE

        authViewModel.registerUser(name, email, password, ktp, phone, address, selectedRole)
    }

    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etKtp.text?.clear()
        binding.etPhone.text?.clear()
        binding.etAddress.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
        binding.spinnerRole.setSelection(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
