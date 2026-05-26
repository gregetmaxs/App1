package com.kasirku.pro.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.R
import com.kasirku.pro.adapter.TransactionAdapter
import com.kasirku.pro.databinding.FragmentTransactionHistoryBinding
import com.kasirku.pro.viewmodel.ReportViewModel

class TransactionHistoryFragment : Fragment() {

    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter {}
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionHistoryFragment.adapter
        }

        viewModel.loadTodayReport()

        viewModel.transactionsByRange.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
            binding.tvEmpty.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipToday) -> viewModel.loadTodayReport()
                checkedIds.contains(R.id.chipWeek) -> viewModel.loadWeeklyReport()
                checkedIds.contains(R.id.chipMonth) -> viewModel.loadMonthlyReport()
                checkedIds.contains(R.id.chipAll) -> viewModel.loadYearlyReport()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
