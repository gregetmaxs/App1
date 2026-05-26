package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.repository.TransactionRepository
import com.kasirku.pro.util.DateUtils
import kotlinx.coroutines.launch

data class DailySales(
    val dayLabel: String,
    val total: Double
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as KasirKuApp).database
    private val transactionRepository = TransactionRepository(database.transactionDao())

    private val _todaySales = MutableLiveData<Double>()
    val todaySales: LiveData<Double> = _todaySales

    private val _todayTransactionCount = MutableLiveData<Int>()
    val todayTransactionCount: LiveData<Int> = _todayTransactionCount

    private val _monthlySales = MutableLiveData<Double>()
    val monthlySales: LiveData<Double> = _monthlySales

    private val _monthlyTransactionCount = MutableLiveData<Int>()
    val monthlyTransactionCount: LiveData<Int> = _monthlyTransactionCount

    private val _weeklySalesChart = MutableLiveData<List<DailySales>>()
    val weeklySalesChart: LiveData<List<DailySales>> = _weeklySalesChart

    val recentTransactions: LiveData<List<Transaction>> = transactionRepository
        .getTransactionsByDateRange(DateUtils.getDaysAgo(30), DateUtils.getEndOfDay())
        .map { it.take(20) }

    fun loadDashboardData() {
        viewModelScope.launch {
            val todayStart = DateUtils.getStartOfDay()
            val todayEnd = DateUtils.getEndOfDay()
            _todaySales.value = transactionRepository.getTotalSalesByDateRange(todayStart, todayEnd) ?: 0.0
            _todayTransactionCount.value = transactionRepository.getSalesCountByDateRange(todayStart, todayEnd)

            val monthStart = DateUtils.getStartOfMonth()
            _monthlySales.value = transactionRepository.getTotalSalesByDateRange(monthStart, todayEnd) ?: 0.0
            _monthlyTransactionCount.value = transactionRepository.getSalesCountByDateRange(monthStart, todayEnd)

            val dailySales = mutableListOf<DailySales>()
            val dayNames = arrayOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
            for (i in 6 downTo 0) {
                val dayStart = DateUtils.getStartOfDay(DateUtils.getDaysAgo(i))
                val dayEnd = DateUtils.getEndOfDay(DateUtils.getDaysAgo(i))
                val total = transactionRepository.getTotalSalesByDateRange(dayStart, dayEnd) ?: 0.0
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = dayStart
                val dayName = dayNames[cal.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                dailySales.add(DailySales(dayName, total))
            }
            _weeklySalesChart.value = dailySales
        }
    }
}
