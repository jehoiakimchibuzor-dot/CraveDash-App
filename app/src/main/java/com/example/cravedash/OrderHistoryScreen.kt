package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * =================================================================================
 * ORDER HISTORY SCREEN: The user's travel log of food.
 * This screen lists every past order with dates, prices, and status.
 * =================================================================================
 */

// --- DATA BLUEPRINT ---
data class PastOrder(
    val id: String,
    val date: String,
    val itemsSummary: String,
    val total: String,
    val status: String,
    val firstItemImage: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(onBackClick: () -> Unit) {
    
    // --- SAMPLE DATABASE ---
    // In a real app, this would be fetched from a server.
    val pastOrders = listOf(
        PastOrder("ORD-9921", "24 Oct, 2023", "2x Spicy Noodles, 1x Coke", "₦3,300", "Delivered", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400"),
        PastOrder("ORD-8812", "15 Oct, 2023", "1x Grilled Salmon", "₦5,000", "Delivered", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400"),
        PastOrder("ORD-7705", "02 Oct, 2023", "3x Meat Pie, 1x Water", "₦1,700", "Cancelled", "https://images.unsplash.com/photo-1601050633729-195085878d96?w=400")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // --- THE ORDER LIST ---
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(pastOrders) { order ->
                    OrderHistoryCard(order)
                }
            }
        }
    }
}

/**
 * REUSABLE COMPONENT: A card for each past order.
 * Shows a summary of the order so the user can recognize it instantly.
 */
@Composable
fun OrderHistoryCard(order: PastOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini image of the first item ordered
            AsyncImage(
                model = order.firstItemImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = order.id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                    Text(text = order.date, fontSize = 12.sp, color = Color.LightGray)
                }
                Text(text = order.itemsSummary, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = order.total, color = Color(0xFFFF8C00), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(12.dp))
                    // Status Badge
                    Surface(
                        color = if (order.status == "Delivered") Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = order.status, 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (order.status == "Delivered") Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}
