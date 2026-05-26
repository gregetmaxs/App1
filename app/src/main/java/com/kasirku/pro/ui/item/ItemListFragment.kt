package com.kasirku.pro.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.kasirku.pro.R
import com.kasirku.pro.adapter.ItemGridAdapter
import com.kasirku.pro.data.entity.Category
import com.kasirku.pro.databinding.FragmentItemListBinding
import com.kasirku.pro.viewmodel.ItemViewModel

class ItemListFragment : Fragment() {

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by viewModels()
    private lateinit var adapter: ItemGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spanCount = resources.getDimension(R.dimen.item_grid_columns).toInt()

        adapter = ItemGridAdapter { item ->
            val bundle = Bundle().apply { putString("itemId", item.id) }
            findNavController().navigate(R.id.action_items_to_addEdit, bundle)
        }

        binding.rvItems.apply {
            layoutManager = GridLayoutManager(requireContext(), spanCount)
            adapter = this@ItemListFragment.adapter
        }

        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_items_to_addEdit)
        }

        binding.btnAddCategory.setOnClickListener { showAddCategoryDialog() }
    }

    private fun showAddCategoryDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Nama Kategori"
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Kategori")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.saveCategory(Category(name = name))
                    Toast.makeText(requireContext(), "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
