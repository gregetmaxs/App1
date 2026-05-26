package com.kasirku.owner.ui.screen.pricing

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.core.model.LicensePrice
import com.kasirku.core.model.User
import com.kasirku.core.util.Constants
import com.kasirku.owner.ui.theme.BluePrimary
import com.kasirku.owner.ui.theme.DarkBackground
import com.kasirku.owner.ui.theme.DarkCard
import com.kasirku.owner.ui.theme.ErrorRed
import com.kasirku.owner.viewmodel.LicenseViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PricingScreen(
    user: User?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LicenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val rupiahFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Avatar + Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(BluePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user?.fullName?.ifEmpty { "Owner" } ?: "Owner", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("Super Admin", style = MaterialTheme.typography.labelSmall, color = BluePrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Harga License", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(uiState.prices) { price ->
            PriceCard(
                price = price,
                rupiahFormat = rupiahFormat,
                onUpdate = { newPrice -> viewModel.updatePrice(price.copy(price = newPrice)) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PriceCard(
    price: LicensePrice,
    rupiahFormat: NumberFormat,
    onUpdate: (Long) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var priceText by remember(price.price) { mutableStateOf(price.price.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    Constants.LICENSE_LABELS[price.type] ?: price.type,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                if (!editing) {
                    Text(
                        rupiahFormat.format(price.price),
                        color = BluePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (editing) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color(0xFF444466)
                    ),
                    modifier = Modifier.width(140.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        priceText.toLongOrNull()?.let { onUpdate(it) }
                        editing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK", fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = { editing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Edit", color = BluePrimary, fontSize = 12.sp)
                }
            }
        }
    }
}
