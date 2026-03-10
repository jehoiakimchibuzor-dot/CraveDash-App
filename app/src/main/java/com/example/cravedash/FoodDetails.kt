package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
 * FOOD DETAILS SCREEN: The high-end "Deep Dive" for every dish.
 * Final Check: High contrast black text and professional layout for Git push.
 * =================================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsScreen(
    itemName: String,
    itemPrice: String,
    description: String, 
    itemImage: String,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit 
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Details", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color.Black) {
                                    Text("3", color = Color.White)
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping Cart", tint = Color.Black)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Button(
                    onClick = onCartClick, 
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black), // High contrast Black
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add to Cart", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { innerPadding -> 
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- PRODUCT IMAGE ---
            AsyncImage(
                model = itemImage, 
                contentDescription = itemName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- NAME AND PRICE ---
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemName, 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color.Black
                )
                Text(
                    text = itemPrice, 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black 
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- PRODUCT DESCRIPTION ---
            Text(
                text = "Product Details",
                modifier = Modifier.align(Alignment.Start),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            val realisticDescription = "$description\n\n" +
                "Our chefs prepare this meal using only the freshest, locally sourced ingredients to ensure every bite is a burst of authentic flavor. " +
                "We take pride in our slow-cooking process that locks in moisture and nutrients, providing you with a meal that is as healthy as it is delicious.\n\n" +
                "Portion Size: Standard XL\n" +
                "Prep Time: 15-20 minutes\n" +
                "Allergens: Contains dairy and traces of nuts. Please inform us of any specific dietary requirements in the checkout notes.\n\n" +
                "Experience the premium CraveDash standard today. Every order is packaged in our eco-friendly, heat-insulated containers to ensure it arrives at your doorstep fresh and steaming hot."

            Text(
                text = realisticDescription, 
                fontSize = 16.sp, 
                color = Color.DarkGray, 
                lineHeight = 26.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
