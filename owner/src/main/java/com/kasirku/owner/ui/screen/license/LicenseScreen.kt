package com.kasirku.owner.ui.screen.license

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.core.model.License
import com.kasirku.core.util.Constants
import com.kasirku.owner.ui.theme.BluePrimary
import com.kasirku.owner.ui.theme.DarkBackground
import com.kasirku.owner.ui.theme.DarkCard
import com.kasirku.owner.ui.theme.DarkSurface
import com.kasirku.owner.ui.theme.ErrorRed
import com.kasirku.owner.ui.theme.SuccessGreen
import com.kasirku.owner.viewmodel.LicenseViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(
    userId: String = "",
    modifier: Modifier = Modifier,
    viewModel: LicenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val rupiahFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id")) }
    val context = LocalContext.current
    var showGenerate by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "License Management",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )

            if (uiState.licenses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada license. Tap + untuk generate.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.licenses) { license ->
                        LicenseCard(license, rupiahFormat, dateFormat, context)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { showGenerate = true },
            containerColor = BluePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Generate License")
        }

        if (showGenerate) {
            ModalBottomSheet(
                onDismissRequest = { showGenerate = false; viewModel.clearGenerated() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = DarkSurface
            ) {
                GenerateLicenseSheet(
                    stores = uiState.stores,
                    prices = uiState.prices,
                    isGenerating = uiState.isGenerating,
                    generatedLicense = uiState.generatedLicense,
                    error = uiState.error,
                    userId = userId,
                    rupiahFormat = rupiahFormat,
                    onGenerate = { storeId, type -> viewModel.generateLicense(storeId, type, userId) },
                    onCopy = { key ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("License Key", key))
                        Toast.makeText(context, "License key disalin!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
private fun LicenseCard(license: License, rupiahFormat: NumberFormat, dateFormat: SimpleDateFormat, context: Context) {
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
                Text(license.licenseKey, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(Constants.LICENSE_LABELS[license.type] ?: license.type, color = Color.Gray, fontSize = 12.sp)
                if (license.expiresAt > 0) {
                    Text("Exp: ${dateFormat.format(Date(license.expiresAt))}", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(rupiahFormat.format(license.price), color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (license.status == "ACTIVE") "Aktif" else "Expired",
                    color = if (license.status == "ACTIVE") SuccessGreen else ErrorRed,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("License Key", license.licenseKey))
                Toast.makeText(context, "Disalin: ${license.licenseKey}", Toast.LENGTH_SHORT).show()
            }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenerateLicenseSheet(
    stores: List<com.kasirku.core.model.Store>,
    prices: List<com.kasirku.core.model.LicensePrice>,
    isGenerating: Boolean,
    generatedLicense: License?,
    error: String?,
    userId: String,
    rupiahFormat: NumberFormat,
    onGenerate: (String, String) -> Unit,
    onCopy: (String) -> Unit
) {
    var selectedStoreId by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var expandedStore by remember { mutableStateOf(false) }
    var expandedType by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BluePrimary,
        unfocusedBorderColor = Color(0xFF444466),
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text("Generate License Baru", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (generatedLicense != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("License berhasil dibuat!", color = SuccessGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(generatedLicense.licenseKey, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onCopy(generatedLicense.licenseKey) }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color.White)
                        }
                    }
                }
            }
        } else {
            // Store selection
            ExposedDropdownMenuBox(expanded = expandedStore, onExpandedChange = { expandedStore = it }) {
                OutlinedTextField(
                    value = stores.find { it.id == selectedStoreId }?.name?.ifEmpty { stores.find { it.id == selectedStoreId }?.ownerEmail } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Toko") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStore) },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedStore, onDismissRequest = { expandedStore = false }) {
                    stores.forEach { store ->
                        DropdownMenuItem(
                            text = { Text(store.name.ifEmpty { store.ownerEmail }) },
                            onClick = { selectedStoreId = store.id; expandedStore = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // License type selection
            ExposedDropdownMenuBox(expanded = expandedType, onExpandedChange = { expandedType = it }) {
                OutlinedTextField(
                    value = Constants.LICENSE_LABELS[selectedType] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipe License") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                    Constants.LICENSE_LABELS.forEach { (type, label) ->
                        val price = prices.find { it.type == type }?.price ?: 0L
                        DropdownMenuItem(
                            text = { Text("$label - ${rupiahFormat.format(price)}") },
                            onClick = { selectedType = type; expandedType = false }
                        )
                    }
                }
            }

            if (selectedType.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val selectedPrice = prices.find { it.type == selectedType }?.price ?: 0L
                Text("Harga: ${rupiahFormat.format(selectedPrice)}", color = BluePrimary, fontWeight = FontWeight.SemiBold)
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error, color = ErrorRed)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedStoreId.isNotEmpty() && selectedType.isNotEmpty()) {
                        onGenerate(selectedStoreId, selectedType)
                    }
                },
                enabled = !isGenerating && selectedStoreId.isNotEmpty() && selectedType.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.padding(4.dp))
                } else {
                    Text("Generate License", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
