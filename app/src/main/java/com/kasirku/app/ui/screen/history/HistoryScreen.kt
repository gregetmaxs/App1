package com.kasirku.app.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.app.ui.theme.DarkBackground
import com.kasirku.app.ui.theme.DarkCard
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.ui.theme.WarningAmber
import com.kasirku.app.viewmodel.HistoryViewModel
import com.kasirku.core.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    storeId: String,
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val transactions by historyViewModel.transactions.collectAsState()
    val rupiahFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id")) }

    LaunchedEffect(storeId) {
        if (storeId.isNotEmpty()) historyViewModel.loadAll(storeId)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Text(
                text = "Riwayat Transaksi",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${transactions.size} transaksi tercatat",
                fontSize = 13.sp,
                color = Color(0xFF9999BB)
            )
        }

        if (transactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = Color(0xFF555577),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Belum ada riwayat transaksi", color = Color(0xFF7777AA), fontSize = 14.sp)
                        }
                    }
                }
            }
        } else {
            items(transactions) { tx ->
                TransactionCard(tx, rupiahFormat, dateFormat)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun TransactionCard(
    tx: Transaction,
    rupiahFormat: NumberFormat,
    dateFormat: SimpleDateFormat
) {
    val isDelivery = tx.deliveryType == "STORE_DELIVERY"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDelivery) WarningAmber.copy(alpha = 0.12f) else TealPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDelivery) Icons.Default.LocalShipping else Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = if (isDelivery) WarningAmber else TealPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.transactionNumber,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${tx.cashierName} \u2022 ${if (isDelivery) "Dikirim Toko" else "Ambil Sendiri"}",
                    fontSize = 11.sp,
                    color = Color(0xFF8888AA)
                )
                Text(
                    text = dateFormat.format(Date(tx.createdAt)),
                    fontSize = 11.sp,
                    color = Color(0xFF7777AA)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = rupiahFormat.format(tx.total),
                    fontSize = 14.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tx.paymentMethod,
                    fontSize = 11.sp,
                    color = Color(0xFF8888AA)
                )
            }
        }
    }
}
