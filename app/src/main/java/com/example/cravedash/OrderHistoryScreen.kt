package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class PastOrder(val id: String, val date: String, val itemsSummary: String, val total: String, val status: String, val firstItemImage: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(onBackClick: () -> Unit) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val card    = if (isDark) Color(0xFF252525) else Color.White

    val orders = listOf(
        PastOrder("ORD-9921", "24 Oct, 2025", "2x Jollof Rice, 1x Chapman", "₦5,200", "Delivered", "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=400"),
        PastOrder("ORD-8812", "15 Oct, 2025", "1x Grilled Salmon, 1x Smoothie", "₦6,500", "Delivered", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400"),
        PastOrder("ORD-7705", "02 Oct, 2025", "3x Meat Pie, 1x Zobo Drink", "₦1,900", "Cancelled", "https://images.unsplash.com/photo-1601050633729-195085878d96?w=400"),
        PastOrder("ORD-6643", "18 Sep, 2025", "1x Pounded Yam, 1x Suya", "₦5,500", "Delivered", "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order History", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        containerColor = bg
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(orders) { order ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = order.firstItemImage, contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.size(62.dp).clip(RoundedCornerShape(12.dp)).background(sub.copy(0.1f)))
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = sub)
                                Text(order.date, fontSize = 11.sp, color = sub)
                            }
                            Text(order.itemsSummary, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = onBg, maxLines = 1)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(order.total, color = Color(0xFFFF8C00), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                Spacer(Modifier.width(10.dp))
                                val delivered = order.status == "Delivered"
                                Surface(
                                    color = if (delivered) (if (isDark) Color(0xFF1A3A1A) else Color(0xFFE8F5E9))
                                            else (if (isDark) Color(0xFF3A1A1A) else Color(0xFFFFEBEE)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(order.status,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                        color = if (delivered) Color(0xFF2E7D32) else Color(0xFFC62828))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
