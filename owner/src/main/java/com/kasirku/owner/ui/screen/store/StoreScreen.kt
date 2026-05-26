package com.kasirku.owner.ui.screen.store

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
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.core.util.Constants
import com.kasirku.owner.ui.theme.BluePrimary
import com.kasirku.owner.ui.theme.DarkBackground
import com.kasirku.owner.ui.theme.DarkCard
import com.kasirku.owner.ui.theme.ErrorRed
import com.kasirku.owner.ui.theme.SuccessGreen
import com.kasirku.owner.viewmodel.StoreViewModel
import com.kasirku.owner.viewmodel.StoreWithLicense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val stores by viewModel.stores.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Text(
            text = "Daftar Toko",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        if (stores.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Belum ada toko terdaftar", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stores) { storeWithLicense ->
                    StoreCard(storeWithLicense, dateFormat)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun StoreCard(item: StoreWithLicense, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(BluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.store.name.ifEmpty { "Toko" }, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(item.store.ownerEmail, color = Color.Gray, fontSize = 12.sp)
                if (item.license != null) {
                    Text(
                        text = "License: ${Constants.LICENSE_LABELS[item.license.type] ?: item.license.type}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                val isActive = item.license?.let { it.status == "ACTIVE" && (it.expiresAt == 0L || it.expiresAt > System.currentTimeMillis()) } ?: false
                Text(
                    text = if (isActive) "Aktif" else "Tidak Aktif",
                    color = if (isActive) SuccessGreen else ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.license?.expiresAt != null && item.license.expiresAt > 0) {
                    Text(
                        text = dateFormat.format(Date(item.license.expiresAt)),
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
