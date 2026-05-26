package com.kasirku.pro.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kasirku.pro.R
import com.kasirku.pro.adapter.TransactionAdapter
import com.kasirku.pro.databinding.FragmentDashboardBinding
import com.kasirku.pro.util.CurrencyFormatter
import com.kasirku.pro.util.SessionManager
import com.kasirku.pro.viewmodel.AuthViewModel
import com.kasirku.pro.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        binding.tvWelcome.text = "Halo, ${sessionManager.getUserName()} (${sessionManager.getRoleName()})"

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.loginFragment)
        }

        transactionAdapter = TransactionAdapter {}
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

        setupChart()
        observeData()
        dashboardViewModel.loadDashboardData()
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            animateY(500)
        }
    }

    private fun observeData() {
        dashboardViewModel.todaySales.observe(viewLifecycleOwner) {
            binding.tvTodaySales.text = CurrencyFormatter.formatSimple(it)
        }

        dashboardViewModel.todayTransactionCount.observe(viewLifecycleOwner) {
            binding.tvTodayCount.text = "$it transaksi"
        }

        dashboardViewModel.monthlySales.observe(viewLifecycleOwner) {
            binding.tvMonthlySales.text = CurrencyFormatter.formatSimple(it)
        }

        dashboardViewModel.weeklySalesChart.observe(viewLifecycleOwner) { dailySales ->
            val entries = dailySales.mapIndexed { index, sales ->
                BarEntry(index.toFloat(), sales.total.toFloat())
            }
            val labels = dailySales.map { it.dayLabel }

            val dataSet = BarDataSet(entries, "Penjualan").apply {
                color = Color.parseColor("#009688")
                valueTextSize = 10f
            }

            binding.barChart.data = BarData(dataSet)
            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            binding.barChart.invalidate()
        }

        dashboardViewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            binding.tvNoTransactions.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.loadDashboardData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
