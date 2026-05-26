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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    androidx.compose.runtime.LaunchedEffect(storeId) {
        if (storeId.isNotEmpty()) posViewModel.init(storeId, userId, userName)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { posViewModel.search(it) },
                placeholder = { Text("Cari produk atau scan barcode...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFF444466),
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Category chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategoryId == null,
                        onClick = { posViewModel.filterByCategory(null) },
                        label = { Text("Semua") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(uiState.categories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategoryId == category.id,
                        onClick = { posViewModel.filterByCategory(category.id) },
                        label = { Text(category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product grid
            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada produk", color = Color.Gray)
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
                        ProductCard(
                            product = product,
                            rupiahFormat = rupiahFormat,
                            onClick = { posViewModel.addToCart(product) }
                        )
                    }
                }
            }

            // Bottom bar with total
            if (uiState.cart.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${uiState.cartCount} item", color = Color.Gray, fontSize = 12.sp)
                            Text(
                                rupiahFormat.format(uiState.total),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Button(
                            onClick = { showCart = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Checkout")
                        }
                    }
                }
            }
        }

        // Cart FAB
        if (uiState.cart.isNotEmpty()) {
            BadgedBox(
                badge = {
                    Badge(containerColor = ErrorRed) {
                        Text("${uiState.cartCount}")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp)
            ) {}
        }

        // Cart bottom sheet
        if (showCart) {
            ModalBottomSheet(
                onDismissRequest = { showCart = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = DarkSurface
            ) {
                CartSheet(
                    cart = uiState.cart,
                    subtotal = uiState.subtotal,
                    ppnAmount = uiState.ppnAmount,
                    total = uiState.total,
                    rupiahFormat = rupiahFormat,
                    onUpdateQuantity = { index, qty -> posViewModel.updateCartQuantity(index, qty) },
                    onRemove = { posViewModel.removeFromCart(it) },
                    onCheckout = { method, paid, delivery ->
                        posViewModel.checkout(method, paid, delivery)
                        showCart = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    rupiahFormat: NumberFormat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (product.categoryName.isNotEmpty()) {
                Text(
                    text = product.categoryName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rupiahFormat.format(product.sellPrice),
                style = MaterialTheme.typography.titleSmall,
                color = TealPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Stok: ${product.stock}",
                style = MaterialTheme.typography.labelSmall,
                color = if (product.stock > product.minStock) Color.Gray else ErrorRed
            )
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
    onUpdateQuantity: (Int, Int) -> Unit,
    onRemove: (Int) -> Unit,
    onCheckout: (String, Long, String) -> Unit
) {
    var paymentMethod by remember { mutableStateOf("CASH") }
    var amountPaid by remember { mutableStateOf("") }
    var deliveryType by remember { mutableStateOf("SELF_PICKUP") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text("Keranjang", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        cart.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.displayName, color = Color.White, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(rupiahFormat.format(item.price), color = Color.Gray, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onUpdateQuantity(index, item.quantity - 1) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", color = Color.White, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
                    IconButton(onClick = { onUpdateQuantity(index, item.quantity + 1) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { onRemove(index) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Divider(color = Color(0xFF333355), modifier = Modifier.padding(vertical = 8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray, fontSize = 13.sp)
            Text(rupiahFormat.format(subtotal), color = Color.White, fontSize = 13.sp)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("PPN (11%)", color = Color.Gray, fontSize = 13.sp)
            Text(rupiahFormat.format(ppnAmount), color = Color.White, fontSize = 13.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total", color = Color.White, fontWeight = FontWeight.Bold)
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
                        selectedLabelColor = Color.White
                    )
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
                        selectedLabelColor = Color.White
                    )
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color(0xFF444466)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val paid = if (paymentMethod == "CASH") (amountPaid.toLongOrNull() ?: total) else total
                onCheckout(paymentMethod, paid, deliveryType)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
        ) {
            Text("Bayar ${rupiahFormat.format(total)}", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
