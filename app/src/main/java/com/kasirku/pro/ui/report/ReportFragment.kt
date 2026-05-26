package com.kasirku.pro.ui.report

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.R
import com.kasirku.pro.adapter.TransactionAdapter
import com.kasirku.pro.databinding.FragmentReportBinding
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.util.DateUtils
import com.kasirku.pro.viewmodel.ReportViewModel
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReportViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter
    private var customStartDate: Long = 0
    private var customEndDate: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TransactionAdapter {}
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ReportFragment.adapter
        }

        setupChipListeners()
        observeData()
        viewModel.loadTodayReport()
    }

    private fun setupChipListeners() {
        binding.chipToday.setOnClickListener {
            binding.layoutDateRange.visibility = View.GONE
            viewModel.loadTodayReport()
        }
        binding.chipWeek.setOnClickListener {
            binding.layoutDateRange.visibility = View.GONE
            viewModel.loadWeeklyReport()
        }
        binding.chipMonth.setOnClickListener {
            binding.layoutDateRange.visibility = View.GONE
            viewModel.loadMonthlyReport()
        }
        binding.chipYear.setOnClickListener {
            binding.layoutDateRange.visibility = View.GONE
            viewModel.loadYearlyReport()
        }
        binding.chipCustom.setOnClickListener {
            binding.layoutDateRange.visibility = View.VISIBLE
        }

        binding.btnStartDate.setOnClickListener { showDatePicker(true) }
        binding.btnEndDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)
            val timestamp = cal.timeInMillis
            if (isStart) {
                customStartDate = timestamp
                binding.btnStartDate.text = DateUtils.formatDate(timestamp)
            } else {
                customEndDate = timestamp
                binding.btnEndDate.text = DateUtils.formatDate(timestamp)
            }
            if (customStartDate > 0 && customEndDate > 0) {
                viewModel.loadCustomReport(customStartDate, customEndDate)
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun observeData() {
        viewModel.reportSummary.observe(viewLifecycleOwner) { summary ->
            binding.tvTotalSales.text = CurrencyFormatter.formatSimple(summary.totalSales)
            binding.tvTotalPurchases.text = CurrencyFormatter.formatSimple(summary.totalPurchases)
            binding.tvProfit.text = CurrencyFormatter.formatSimple(summary.profit)
            binding.tvTransactionCount.text = "${summary.transactionCount} transaksi"
        }

        viewModel.transactionsByRange.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
