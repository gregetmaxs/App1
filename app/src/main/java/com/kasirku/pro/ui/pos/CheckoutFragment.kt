package com.kasirku.pro.ui.pos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.adapter.CartAdapter
import com.kasirku.pro.data.entity.DeliveryType
import com.kasirku.pro.data.entity.User
import com.kasirku.pro.databinding.FragmentCheckoutBinding
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.viewmodel.AuthViewModel
import com.kasirku.pro.viewmodel.PosViewModel

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val posViewModel: PosViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private var employees: List<User> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartAdapter = CartAdapter(
            onQuantityChanged = { position, quantity -> posViewModel.updateCartQuantity(position, quantity) },
            onRemoveClick = { position -> posViewModel.removeFromCart(position) }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        posViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items.toList())
        }

        posViewModel.totalBill.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = total
        }

        binding.etPaidAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val paid = s.toString().toDoubleOrNull() ?: 0.0
                val total = posViewModel.totalBillDouble.value ?: 0.0
                val change = paid - total
                binding.tvChange.text = "Kembalian: ${CurrencyFormatter.formatSimple(if (change > 0) change else 0.0)}"
            }
        })

        binding.rgDeliveryType.setOnCheckedChangeListener { _, checkedId ->
            binding.layoutDeliveryDetails.visibility =
                if (checkedId == binding.rbDelivery.id) View.VISIBLE else View.GONE
        }

        authViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            employees = users
            val names = listOf("- Pilih -") + users.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            binding.spinnerDriver.adapter = adapter
            binding.spinnerHelper.adapter = adapter
        }

        binding.btnProcessPayment.setOnClickListener { processPayment() }

        posViewModel.checkoutResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { transaction ->
                Toast.makeText(requireContext(), "Transaksi berhasil!\n${transaction.resiNumber}", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
            result.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processPayment() {
        val customerName = binding.etCustomerName.text.toString().trim()
        val paidStr = binding.etPaidAmount.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (paidStr.isEmpty()) {
            Toast.makeText(requireContext(), "Masukkan jumlah bayar", Toast.LENGTH_SHORT).show()
            return
        }

        val paidAmount = paidStr.toDoubleOrNull() ?: 0.0
        val isDelivery = binding.rbDelivery.isChecked
        val deliveryType = if (isDelivery) DeliveryType.DELIVERY else DeliveryType.PICKUP

        var deliveryAddress = ""
        var driverId = ""
        var driverName = ""
        var helperId = ""
        var helperName = ""

        if (isDelivery) {
            deliveryAddress = binding.etDeliveryAddress.text.toString().trim()
            val driverPos = binding.spinnerDriver.selectedItemPosition
            val helperPos = binding.spinnerHelper.selectedItemPosition

            if (driverPos > 0 && driverPos <= employees.size) {
                val driver = employees[driverPos - 1]
                driverId = driver.id
                driverName = driver.name
            }
            if (helperPos > 0 && helperPos <= employees.size) {
                val helper = employees[helperPos - 1]
                helperId = helper.id
                helperName = helper.name
            }
        }

        posViewModel.checkout(
            customerName, paidAmount, notes, deliveryType,
            deliveryAddress, driverId, driverName, helperId, helperName
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
