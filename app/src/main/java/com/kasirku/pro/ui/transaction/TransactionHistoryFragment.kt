package com.kasirku.pro.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.adapter.TransactionAdapter
import com.kasirku.pro.databinding.FragmentDeliveryBinding
import com.kasirku.pro.viewmodel.ReportViewModel

class TransactionHistoryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter {}
        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionHistoryFragment.adapter
        }

        viewModel.loadTodayReport()

        viewModel.transactionsByRange.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.tvEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, _ ->
            // Reuse chips for date filtering
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
