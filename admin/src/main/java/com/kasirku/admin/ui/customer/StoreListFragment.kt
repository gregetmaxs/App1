package com.kasirku.admin.ui.customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.admin.adapter.StoreAdapter
import com.kasirku.admin.databinding.FragmentStoreListBinding
import com.kasirku.admin.viewmodel.LicenseViewModel

class StoreListFragment : Fragment() {

    private var _binding: FragmentStoreListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LicenseViewModel by activityViewModels()
    private lateinit var adapter: StoreAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StoreAdapter()

        binding.rvStores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StoreListFragment.adapter
        }

        viewModel.allStores.observe(viewLifecycleOwner) { stores ->
            adapter.submitList(stores)
            binding.tvEmpty.visibility = if (stores.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    viewModel.allStores.observe(viewLifecycleOwner) { adapter.submitList(it) }
                } else {
                    viewModel.searchStores("%$query%").observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
