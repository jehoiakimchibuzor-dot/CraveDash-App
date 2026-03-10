package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * =================================================================================
 * PROFILE SCREEN: The user's personal control center.
 * This screen manages account details, order history, and security settings.
 * =================================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressesClick: () -> Unit
) {
    Scaffold(
        // --- TOP NAVIGATION ---
        topBar = {
            TopAppBar(
                title = { Text("Account", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F8F8) // Subtle background to make sections stand out
    ) { innerPadding ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // --- SECTION 1: USER IDENTITY ---
            // Grabbing a professional avatar from the web
            AsyncImage(
                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400",
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Emmanuella Maxwell",
                fontSize = 24.sp, 
                fontWeight = FontWeight.ExtraBold, 
                color = Color.Black
            )
            Text(
                text = "EmmanuellaOkene@gmail.com",
                fontSize = 14.sp, 
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION 2: ACCOUNT MANAGEMENT ---
            Text(
                text = "Account Activity", 
                modifier = Modifier.align(Alignment.Start),
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileMenuItem(icon = Icons.Default.History, label = "My Orders", onClick = onOrdersClick)
            ProfileMenuItem(icon = Icons.Default.LocationOn, label = "Saved Addresses", onClick = onAddressesClick)
            ProfileMenuItem(icon = Icons.Default.CreditCard, label = "Payment Methods", onClick = {})
            
            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTION 3: APP SETTINGS ---
            Text(
                text = "App Settings", 
                modifier = Modifier.align(Alignment.Start),
                fontSize = 18.sp, 
                fontWeight = FontWeight.Bold, 
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileMenuItem(icon = Icons.Default.Notifications, label = "Notifications", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Security, label = "Security & Privacy", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Help, label = "Help Center", onClick = {})

            Spacer(modifier = Modifier.height(40.dp))

            // --- LOGOUT BUTTON ---
            // Styled in Red to signify a significant action
            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEEBEB)),
                shape = RoundedCornerShape(12.dp),
                elevation = null
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Log Out", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * REUSABLE COMPONENT: A sleek, standard row for profile options.
 * Features an icon, label, and an arrow to indicate it's clickable.
 */
@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = Color(0xFFFF8C00), // Our brand orange for accents
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label, 
                modifier = Modifier.weight(1f), 
                fontSize = 16.sp, 
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                contentDescription = null, 
                tint = Color.LightGray
            )
        }
    }
}
