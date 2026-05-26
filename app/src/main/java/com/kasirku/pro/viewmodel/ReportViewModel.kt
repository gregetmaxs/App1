package com.kasirku.pro.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.pro.KasirKuApp
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.data.entity.TransactionType
import com.kasirku.pro.data.repository.TransactionRepository
import com.kasirku.pro.util.DateUtils
import kotlinx.coroutines.launch

data class ReportSummary(
    val totalSales: Double = 0.0,
    val totalPurchases: Double = 0.0,
    val profit: Double = 0.0,
    val transactionCount: Int = 0,
    val transactions: List<Transaction> = emptyList()
)

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as KasirKuApp).database
    private val transactionRepository = TransactionRepository(database.transactionDao())

    private val _reportSummary = MutableLiveData<ReportSummary>()
    val reportSummary: LiveData<ReportSummary> = _reportSummary

    private val _dateRange = MutableLiveData<Pair<Long, Long>>()
    val dateRange: LiveData<Pair<Long, Long>> = _dateRange

    val transactionsByRange: LiveData<List<Transaction>> = _dateRange.switchMap { range ->
        transactionRepository.getTransactionsByDateRange(range.first, range.second)
    }

    fun loadTodayReport() {
        val start = DateUtils.getStartOfDay()
        val end = DateUtils.getEndOfDay()
        loadReport(start, end)
    }

    fun loadWeeklyReport() {
        val start = DateUtils.getStartOfWeek()
        val end = DateUtils.getEndOfDay()
        loadReport(start, end)
    }

    fun loadMonthlyReport() {
        val start = DateUtils.getStartOfMonth()
        val end = DateUtils.getEndOfDay()
        loadReport(start, end)
    }

    fun loadYearlyReport() {
        val start = DateUtils.getStartOfYear()
        val end = DateUtils.getEndOfDay()
        loadReport(start, end)
    }

    fun loadCustomReport(startDate: Long, endDate: Long) {
        val start = DateUtils.getStartOfDay(startDate)
        val end = DateUtils.getEndOfDay(endDate)
        loadReport(start, end)
    }

    private fun loadReport(start: Long, end: Long) {
        _dateRange.value = Pair(start, end)
        viewModelScope.launch {
            val totalSales = transactionRepository.getTotalSalesByDateRange(start, end) ?: 0.0
            val totalPurchases = transactionRepository.getTotalPurchasesByDateRange(start, end) ?: 0.0
            val count = transactionRepository.getSalesCountByDateRange(start, end)

            _reportSummary.value = ReportSummary(
                totalSales = totalSales,
                totalPurchases = totalPurchases,
                profit = totalSales - totalPurchases,
                transactionCount = count
            )
        }
    }
}
