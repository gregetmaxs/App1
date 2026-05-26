package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.adapter.RoleAdapter
import com.kasirku.pro.data.entity.Role
import com.kasirku.pro.databinding.FragmentDeliveryBinding
import com.kasirku.pro.viewmodel.RoleViewModel

class RoleManagementFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RoleViewModel by viewModels()
    private lateinit var adapter: RoleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RoleAdapter(
            onEditClick = { role -> showRoleDialog(role) },
            onDeleteClick = { role ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Hapus Role")
                    .setMessage("Yakin hapus role '${role.name}'?")
                    .setPositiveButton("Hapus") { _, _ -> viewModel.deleteRole(role) }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RoleManagementFragment.adapter
        }

        viewModel.allRoles.observe(viewLifecycleOwner) { roles ->
            adapter.submitList(roles)
        }
    }

    private fun showRoleDialog(role: Role? = null) {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        val etName = EditText(requireContext()).apply {
            hint = "Nama Role"
            setText(role?.name ?: "")
        }
        layout.addView(etName)

        val permissions = mapOf(
            "Akses POS" to (role?.canAccessPos ?: false),
            "Kelola Item" to (role?.canAccessItemManagement ?: false),
            "Pembelian Stok" to (role?.canAccessPurchase ?: false),
            "Laporan" to (role?.canAccessReport ?: false),
            "Customer" to (role?.canAccessCustomer ?: false),
            "Karyawan" to (role?.canAccessEmployee ?: false),
            "Pengiriman" to (role?.canAccessDelivery ?: false),
            "Foto Bukti Kirim" to (role?.canTakeDeliveryPhoto ?: false),
            "Pengaturan" to (role?.canAccessSettings ?: false),
            "Kelola Role" to (role?.canManageRoles ?: false)
        )

        val checkboxes = mutableMapOf<String, CheckBox>()
        permissions.forEach { (label, checked) ->
            val cb = CheckBox(requireContext()).apply {
                text = label
                isChecked = checked
            }
            checkboxes[label] = cb
            layout.addView(cb)
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (role == null) "Tambah Role" else "Edit Role")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Nama role wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newRole = (role ?: Role()).copy(
                    name = name,
                    canAccessPos = checkboxes["Akses POS"]?.isChecked ?: false,
                    canAccessItemManagement = checkboxes["Kelola Item"]?.isChecked ?: false,
                    canAccessPurchase = checkboxes["Pembelian Stok"]?.isChecked ?: false,
                    canAccessReport = checkboxes["Laporan"]?.isChecked ?: false,
                    canAccessCustomer = checkboxes["Customer"]?.isChecked ?: false,
                    canAccessEmployee = checkboxes["Karyawan"]?.isChecked ?: false,
                    canAccessDelivery = checkboxes["Pengiriman"]?.isChecked ?: false,
                    canTakeDeliveryPhoto = checkboxes["Foto Bukti Kirim"]?.isChecked ?: false,
                    canAccessSettings = checkboxes["Pengaturan"]?.isChecked ?: false,
                    canManageRoles = checkboxes["Kelola Role"]?.isChecked ?: false,
                    isSynced = false
                )

                if (role != null) viewModel.updateRole(newRole) else viewModel.saveRole(newRole)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
