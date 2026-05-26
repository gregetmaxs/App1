package com.kasirku.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kasirku.core.model.Transaction
import com.kasirku.core.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val todaySales: Long = 0L,
    val todayCount: Int = 0,
    val monthSales: Long = 0L,
    val recentTransactions: List<Transaction> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadData(storeId: String) {
        if (storeId.isEmpty()) return
        val cal = Calendar.getInstance()

        // Today
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.timeInMillis
        val todayEnd = todayStart + 24 * 60 * 60 * 1000

        // Month
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val monthEnd = cal.timeInMillis

        viewModelScope.launch {
            transactionRepository.getTotalByDateRange(storeId, todayStart, todayEnd)
                .collect { _uiState.value = _uiState.value.copy(todaySales = it ?: 0L) }
        }
        viewModelScope.launch {
            transactionRepository.getCountByDateRange(storeId, todayStart, todayEnd)
                .collect { _uiState.value = _uiState.value.copy(todayCount = it) }
        }
        viewModelScope.launch {
            transactionRepository.getTotalByDateRange(storeId, monthStart, monthEnd)
                .collect { _uiState.value = _uiState.value.copy(monthSales = it ?: 0L) }
        }
        viewModelScope.launch {
            transactionRepository.getRecent(storeId, 10)
                .collect { _uiState.value = _uiState.value.copy(recentTransactions = it) }
        }
    }
}
