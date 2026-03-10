package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =================================================================================
 * ORDER TRACKING SCREEN: The "Real-Time" status page.
 * This screen shows the user exactly where their food is.
 * =================================================================================
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER: ESTIMATED TIME ---
            Text(text = "Estimated Delivery Time", fontSize = 14.sp, color = Color.Gray)
            Text(text = "25 - 35 mins", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            
            Spacer(modifier = Modifier.height(32.dp))

            // --- TRACKING STEPPER ---
            // Professional vertical line showing the stages of delivery.
            TrackingStep(
                title = "Order Confirmed", 
                subtitle = "We have received your order", 
                icon = Icons.Default.Check, 
                isCompleted = true, 
                showLine = true
            )
            TrackingStep(
                title = "Preparing Food", 
                subtitle = "Your meal is being cooked by our top chefs", 
                icon = Icons.Default.Restaurant, 
                isCompleted = true, 
                showLine = true
            )
            TrackingStep(
                title = "On the Way", 
                subtitle = "Our rider is bringing your food to you", 
                icon = Icons.Default.DeliveryDining, 
                isCompleted = false, 
                isCurrent = true,
                showLine = true
            )
            TrackingStep(
                title = "Delivered", 
                subtitle = "Enjoy your world-class CraveDash meal!", 
                icon = Icons.Default.ShoppingBag, 
                isCompleted = false, 
                showLine = false
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- RIDER INFO CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Rider Photo Placeholder
                    Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray))
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Samuel (Rider)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Text(text = "4.9 ⭐ Rating", fontSize = 12.sp, color = Color.Gray)
                    }
                    
                    // Call Rider Button
                    IconButton(
                        onClick = { /* Handle Call */ },
                        modifier = Modifier.background(Color(0xFFFF8C00), CircleShape)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                    }
                }
            }
        }
    }
}

/**
 * REUSABLE COMPONENT: A single step in the tracking timeline.
 */
@Composable
fun TrackingStep(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    isCompleted: Boolean, 
    isCurrent: Boolean = false,
    showLine: Boolean = true
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Step Icon Circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted || isCurrent) Color(0xFFFF8C00) else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            
            // Connecting Vertical Line
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(if (isCompleted) Color(0xFFFF8C00) else Color.LightGray)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title, 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp, 
                color = if (isCompleted || isCurrent) Color.Black else Color.Gray
            )
            Text(
                text = subtitle, 
                fontSize = 13.sp, 
                color = Color.Gray
            )
        }
    }
}
