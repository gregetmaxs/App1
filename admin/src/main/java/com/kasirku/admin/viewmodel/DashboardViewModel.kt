package com.kasirku.admin.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.kasirku.admin.AdminApp
import com.kasirku.admin.util.DateUtils
import kotlinx.coroutines.launch

data class DashboardData(
    val totalLicenses: Int = 0,
    val activeLicenses: Int = 0,
    val totalStores: Int = 0,
    val totalRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val dailySales: List<DailySale> = emptyList()
)

data class DailySale(
    val label: String = "",
    val count: Int = 0,
    val amount: Double = 0.0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as AdminApp).database
    private val licenseDao = database.licenseDao()
    private val storeDao = database.storeDao()

    private val _dashboardData = MutableLiveData<DashboardData>()
    val dashboardData: LiveData<DashboardData> = _dashboardData

    val recentHistory = database.licenseHistoryDao().getRecentHistory()

    fun loadDashboard() {
        viewModelScope.launch {
            val totalLicenses = licenseDao.getTotalCount()
            val activeLicenses = licenseDao.getActiveCount()
            val totalStores = storeDao.getTotalCount()
            val totalRevenue = licenseDao.getTotalRevenue() ?: 0.0

            val monthStart = DateUtils.getStartOfMonth()
            val monthEnd = DateUtils.getEndOfDay()
            val monthlyRevenue = licenseDao.getRevenueByDateRange(monthStart, monthEnd) ?: 0.0

            val dailySales = mutableListOf<DailySale>()
            val days = arrayOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
            for (i in 6 downTo 0) {
                val dayStart = DateUtils.getDaysAgo(i)
                val dayEnd = DateUtils.getEndOfDay(dayStart)
                val count = licenseDao.getCountByDateRange(dayStart, dayEnd)
                val revenue = licenseDao.getRevenueByDateRange(dayStart, dayEnd) ?: 0.0
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = dayStart
                val dayOfWeek = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                dailySales.add(DailySale(label = days[dayOfWeek], count = count, amount = revenue))
            }

            _dashboardData.value = DashboardData(
                totalLicenses = totalLicenses,
                activeLicenses = activeLicenses,
                totalStores = totalStores,
                totalRevenue = totalRevenue,
                monthlyRevenue = monthlyRevenue,
                dailySales = dailySales
            )
        }
    }
}
