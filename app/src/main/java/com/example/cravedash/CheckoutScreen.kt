package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =================================================================================
 * CHECKOUT SCREEN: The final step before the food is on its way!
 * I've updated the colors to Black as requested to make everything visible.
 * =================================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onPlaceOrderClick: () -> Unit
) {
    Scaffold(
        // --- TOP BAR ---
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // --- BOTTOM PLACEMENT BAR ---
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = onPlaceOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)), // Keeping the main button orange for branding
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Pay & Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- DELIVERY ADDRESS SECTION ---
            // Icons and Titles are now Black for better visibility
            CheckoutSection(title = "Delivery Address", icon = Icons.Default.LocationOn) {
                Text("123 Crave Street, Lagos, Nigeria", fontWeight = FontWeight.Medium, color = Color.Black)
                Text("Phone: +234 800 123 4567", color = Color.DarkGray, fontSize = 14.sp)
            }

            // --- PAYMENT METHOD SECTION ---
            CheckoutSection(title = "Payment Method", icon = Icons.Default.Payment) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("**** **** **** 1234", fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("(Mastercard)", color = Color.DarkGray, fontSize = 14.sp)
                }
            }

            // --- ORDER SUMMARY ---
            // Text colors updated to Black and DarkGray
            CheckoutSection(title = "Order Summary", icon = null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Basket Total", color = Color.Black)
                    Text("₦2,300", color = Color.Black)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Fee", color = Color.Black)
                    Text("₦500", color = Color.Black)
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total to Pay", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                    Text("₦2,800", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black) // Total is now Black and Bold
                }
            }
        }
    }
}

/**
 * A reusable container for each part of the checkout process.
 * Icon tint changed to Black for visibility.
 */
@Composable
fun CheckoutSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    // Tint changed to Black as requested
                    Icon(icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
