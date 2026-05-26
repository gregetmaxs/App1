package com.kasirku.app.ui.screen.master

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kasirku.app.ui.theme.DarkBackground
import com.kasirku.app.ui.theme.DarkCard
import com.kasirku.app.ui.theme.DarkSurface
import com.kasirku.app.ui.theme.ErrorRed
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.SupplierViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierScreen(
    storeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SupplierViewModel = hiltViewModel()
) {
    val suppliers by viewModel.suppliers.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    LaunchedEffect(storeId) { viewModel.loadAll(storeId) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Supplier", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = TealPrimary, shape = CircleShape) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }
            if (suppliers.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = DarkCard)) {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.LocalShipping, null, tint = Color(0xFF555577), modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(12.dp))
                                Text("Belum ada supplier", color = Color(0xFF7777AA), fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                items(suppliers) { sup ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DarkCard)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(44.dp).clip(CircleShape).background(TealPrimary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LocalShipping, null, tint = TealPrimary, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sup.name, fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                if (sup.phone.isNotEmpty()) Text(sup.phone, fontSize = 12.sp, color = Color(0xFF8888AA))
                                if (sup.address.isNotEmpty()) Text(sup.address, fontSize = 11.sp, color = Color(0xFF7777AA), maxLines = 1)
                            }
                            IconButton(onClick = { viewModel.delete(sup.id) }) {
                                Icon(Icons.Default.Delete, null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showDialog) {
        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary, unfocusedBorderColor = Color(0xFF444466),
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedLabelColor = TealPrimary, unfocusedLabelColor = Color(0xFF8888AA)
        )
        AlertDialog(
            onDismissRequest = { showDialog = false; name = ""; phone = ""; address = "" },
            title = { Text("Tambah Supplier", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Supplier *") }, singleLine = true, shape = RoundedCornerShape(14.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No. Telepon") }, singleLine = true, shape = RoundedCornerShape(14.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, singleLine = true, shape = RoundedCornerShape(14.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) { viewModel.create(name, phone, address, storeId); name = ""; phone = ""; address = ""; showDialog = false }
                }) { Text("Simpan", color = TealPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; name = ""; phone = ""; address = "" }) { Text("Batal", color = Color.Gray) }
            },
            containerColor = DarkSurface
        )
    }
}
