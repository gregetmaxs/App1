package com.kasirku.app.ui.screen.pos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kasirku.app.ui.theme.DarkBackground
import com.kasirku.app.ui.theme.DarkCard
import com.kasirku.app.ui.theme.DarkSurface
import com.kasirku.app.ui.theme.ErrorRed
import com.kasirku.app.ui.theme.SuccessGreen
import com.kasirku.app.ui.theme.TealPrimary
import com.kasirku.app.viewmodel.PosViewModel
import com.kasirku.core.model.CartItem
import com.kasirku.core.model.Product
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    modifier: Modifier = Modifier,
    storeId: String = "",
    userId: String = "",
    userName: String = "",
    posViewModel: PosViewModel = hiltViewModel()
) {
    val uiState by posViewModel.uiState.collectAsState()
    val rupiahFormat = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    var showCart by remember { mutableStateOf(false) }

    LaunchedEffect(storeId) {
        if (storeId.isNotEmpty()) posViewModel.init(storeId, userId, userName)
    }

    Box(modifier = modifier.background(DarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { posViewModel.search(it) },
                placeholder = { Text("Cari produk atau scan barcode...", color = Color(0xFF6666AA)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8888AA)) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { posViewModel.search("") }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF8888AA))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryId == null,
                        onClick = { posViewModel.filterByCategory(null) },
                        label = { Text("Semua", fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = DarkCard,
                            labelColor = Color(0xFFAAAACC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(uiState.categories) { cat ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == cat.id,
                        onClick = { posViewModel.filterByCategory(cat.id) },
                        label = { Text(cat.name, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = DarkCard,
                            labelColor = Color(0xFFAAAACC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Product Grid
            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF555577),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Belum ada produk", color = Color(0xFF7777AA), fontSize = 14.sp)
                        Text("Tambahkan produk dari menu Profile", color = Color(0xFF555577), fontSize = 12.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(product, rupiahFormat) {
                            posViewModel.addToCart(product)
                        }
                    }
                }
            }

            // Bottom Cart Bar
            AnimatedVisibility(visible = uiState.cartCount > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCart = true },
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "${uiState.cartCount} item",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            rupiahFormat.format(uiState.total),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        // Cart Bottom Sheet
        if (showCart) {
            ModalBottomSheet(
                onDismissRequest = { showCart = false },
                containerColor = DarkSurface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF555577))
                    )
                }
            ) {
                CartSheet(
                    cart = uiState.cart,
                    subtotal = uiState.subtotal,
                    ppnAmount = uiState.ppnAmount,
                    total = uiState.total,
                    rupiahFormat = rupiahFormat,
                    onQuantityChange = { i, q -> posViewModel.updateCartQuantity(i, q) },
                    onRemove = { posViewModel.removeFromCart(it) },
                    onCheckout = { paymentMethod, amountPaid, deliveryType ->
                        posViewModel.checkout(paymentMethod, amountPaid, deliveryType)
                        showCart = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, rupiahFormat: NumberFormat, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product icon placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2A2A50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(2).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rupiahFormat.format(product.sellPrice),
                    fontSize = 13.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(TealPrimary)
                        .clickable { onAdd() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            if (product.stock > 0) {
                Text("Stok: ${product.stock}", fontSize = 10.sp, color = Color(0xFF8888AA))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartSheet(
    cart: List<CartItem>,
    subtotal: Long,
    ppnAmount: Long,
    total: Long,
    rupiahFormat: NumberFormat,
    onQuantityChange: (Int, Int) -> Unit,
    onRemove: (Int) -> Unit,
    onCheckout: (String, Long, String) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("CASH") }
    var deliveryType by remember { mutableStateOf("SELF_PICKUP") }
    var amountPaid by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp)
    ) {
        Text("Keranjang", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Cart items
        cart.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.product.name, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Text(
                        rupiahFormat.format(item.product.sellPrice),
                        fontSize = 12.sp,
                        color = Color(0xFF9999BB)
                    )
                }
                // Quantity controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(index, item.quantity - 1) },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF333360))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        "${item.quantity}",
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { onQuantityChange(index, item.quantity + 1) },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(TealPrimary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    rupiahFormat.format(item.subtotal),
                    fontSize = 13.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Divider(color = Color(0xFF333355), modifier = Modifier.padding(vertical = 12.dp))

        // Totals
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color(0xFF9999BB), fontSize = 13.sp)
            Text(rupiahFormat.format(subtotal), color = Color.White, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("PPN (11%)", color = Color(0xFF9999BB), fontSize = 13.sp)
            Text(rupiahFormat.format(ppnAmount), color = Color.White, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(rupiahFormat.format(total), color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment method
        Text("Pembayaran", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("CASH" to "Tunai", "TRANSFER" to "Transfer", "QRIS" to "QRIS").forEach { (value, label) ->
                FilterChip(
                    selected = paymentMethod == value,
                    onClick = { paymentMethod = value },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = DarkCard,
                        labelColor = Color(0xFFAAAACC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delivery type
        Text("Pengiriman", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("SELF_PICKUP" to "Ambil Sendiri", "STORE_DELIVERY" to "Dikirim Toko").forEach { (value, label) ->
                FilterChip(
                    selected = deliveryType == value,
                    onClick = { deliveryType = value },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TealPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = DarkCard,
                        labelColor = Color(0xFFAAAACC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        if (paymentMethod == "CASH") {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = amountPaid,
                onValueChange = { amountPaid = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah Bayar") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFF444466),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = TealPrimary,
                    unfocusedLabelColor = Color(0xFF8888AA)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val paid = amountPaid.toLongOrNull() ?: total
                onCheckout(paymentMethod, paid, deliveryType)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
        ) {
            Text("Bayar ${rupiahFormat.format(total)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
