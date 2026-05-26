package com.kasirku.owner.ui.screen.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kasirku.owner.ui.theme.DarkBackground

@Composable
fun StoreScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Manajemen Toko\n(Coming in next step)",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
