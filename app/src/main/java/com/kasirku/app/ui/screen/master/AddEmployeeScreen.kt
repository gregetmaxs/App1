package com.kasirku.app.ui.screen.master

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val roles by viewModel.roles.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedRoleId by remember { mutableStateOf("") }
    var roleExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(storeId) { viewModel.loadData(storeId) }
    LaunchedEffect(uiState.success) { if (uiState.success) onBack() }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TealPrimary,
        unfocusedBorderColor = Color(0xFF444466),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = TealPrimary,
        unfocusedLabelColor = Color(0xFF8888AA),
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Tambah Karyawan", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = fullName, onValueChange = { fullName = it },
                label = { Text("Nama Lengkap *") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nik, onValueChange = { nik = it.filter { c -> c.isDigit() } },
                label = { Text("No. KTP (NIK) *") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it.filter { c -> c.isDigit() || c == '+' } },
                label = { Text("No. HP *") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("Alamat *") },
                minLines = 2, maxLines = 3, shape = RoundedCornerShape(14.dp),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email (untuk login) *") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password (min 6 karakter) *") },
                singleLine = true, shape = RoundedCornerShape(14.dp),
                colors = fieldColors, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Role Dropdown
            ExposedDropdownMenuBox(expanded = roleExpanded, onExpandedChange = { roleExpanded = it }) {
                OutlinedTextField(
                    value = roles.find { it.id == selectedRoleId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = roleExpanded, onDismissRequest = { roleExpanded = false }) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name, color = Color.White) },
                            onClick = { selectedRoleId = role.id; roleExpanded = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            val isValid = fullName.isNotBlank() && nik.length >= 16 && phone.isNotBlank() &&
                    address.isNotBlank() && email.contains("@") && password.length >= 6 &&
                    selectedRoleId.isNotBlank()

            Button(
                onClick = {
                    viewModel.createEmployee(
                        fullName = fullName, email = email, password = password,
                        phone = phone, nik = nik, address = address,
                        roleId = selectedRoleId, storeId = storeId
                    )
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, disabledContainerColor = Color(0xFF333355))
            ) {
                Text("Simpan Karyawan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (uiState.error.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(uiState.error, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
