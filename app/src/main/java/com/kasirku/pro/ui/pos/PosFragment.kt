package com.kasirku.pro.ui.pos

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.kasirku.pro.R
import com.kasirku.pro.adapter.CategoryAdapter
import com.kasirku.pro.adapter.ItemGridAdapter
import com.kasirku.pro.data.entity.Category
import com.kasirku.pro.databinding.FragmentPosBinding
import com.kasirku.pro.viewmodel.PosViewModel

class PosFragment : Fragment() {

    private var _binding: FragmentPosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PosViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemGridAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupSearch()
        observeData()

        binding.btnCheckout.setOnClickListener {
            val cart = viewModel.cartItems.value
            if (cart.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_pos_to_checkout)
        }

        binding.btnScanBarcode.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setPrompt("Scan barcode item")
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
        }
    }

    private fun setupAdapters() {
        val spanCount = resources.getDimension(R.dimen.item_grid_columns).toInt()

        itemAdapter = ItemGridAdapter { item ->
            viewModel.addToCart(item)
            Toast.makeText(requireContext(), "${item.name} ditambahkan", Toast.LENGTH_SHORT).show()
        }

        binding.rvItems.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = itemAdapter
        }

        categoryAdapter = CategoryAdapter { category ->
            viewModel.selectCategory(category?.id)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchItems(s.toString())
            }
        })
    }

    private fun observeData() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            val allCategories = mutableListOf(Category(id = "", name = "Semua"))
            allCategories.addAll(categories)
            categoryAdapter.submitList(allCategories)
        }

        viewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            itemAdapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.totalBill.observe(viewLifecycleOwner) { total ->
            binding.tvTotalBill.text = total
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            binding.tvCartCount.text = if (items.isNotEmpty()) "(${items.size} item)" else ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
