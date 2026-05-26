package com.kasirku.admin.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kasirku.admin.databinding.FragmentDashboardBinding
import com.kasirku.admin.util.CurrencyFormatter
import com.kasirku.admin.viewmodel.DashboardViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart()
        observeData()
        viewModel.loadDashboard()
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
        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            if (data == null) return@observe
            binding.tvTotalLicenses.text = data.totalLicenses.toString()
            binding.tvActiveLicenses.text = data.activeLicenses.toString()
            binding.tvTotalRevenue.text = CurrencyFormatter.format(data.totalRevenue)
            binding.tvMonthlyRevenue.text = CurrencyFormatter.format(data.monthlyRevenue)

            val entries = data.dailySales.mapIndexed { index, sale ->
                BarEntry(index.toFloat(), sale.amount.toFloat())
            }
            val labels = data.dailySales.map { it.label }

            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Penjualan").apply {
                    color = Color.parseColor("#1565C0")
                    valueTextSize = 10f
                }
                binding.barChart.data = BarData(dataSet)
                binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                binding.barChart.invalidate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
