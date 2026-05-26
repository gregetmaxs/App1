package com.kasirku.app.ui.screen.master

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.app.ui.theme.DarkBackground
import com.kasirku.app.ui.theme.DarkCard
import com.kasirku.app.ui.theme.DarkSurface
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.EmployeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    storeId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedRoleId by remember { mutableStateOf("") }
    var expandedRole by remember { mutableStateOf(false) }

    LaunchedEffect(storeId) { viewModel.loadData(storeId) }
    LaunchedEffect(uiState.success) {
        if (uiState.success) { viewModel.clearState(); onBack() }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TealPrimary,
        unfocusedBorderColor = Color(0xFF444466),
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Daftar Karyawan", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nama Lengkap *") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = nik, onValueChange = { nik = it.filter { c -> c.isDigit() } }, label = { Text("No. KTP (NIK) *") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it.filter { c -> c.isDigit() } }, label = { Text("No. HP *") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat *") }, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email *") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password *") }, singleLine = true, shape = RoundedCornerShape(12.dp), colors = fieldColors, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expandedRole, onExpandedChange = { expandedRole = it }) {
                OutlinedTextField(
                    value = uiState.roles.find { it.id == selectedRoleId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedRole, onDismissRequest = { expandedRole = false }) {
                    uiState.roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = { selectedRoleId = role.id; expandedRole = false }
                        )
                    }
                }
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(uiState.error!!, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank() && password.length >= 6 && nik.isNotBlank() && phone.isNotBlank() && address.isNotBlank()) {
                        viewModel.registerEmployee(email, password, fullName, phone, nik, address, selectedRoleId, storeId)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Daftar Karyawan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
