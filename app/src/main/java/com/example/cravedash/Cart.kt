package com.example.cravedash

import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
 * CART SCREEN: Where the hunger turns into an order.
 * Updated: Extended Quick-Add section with all categories (Meals, Sides, Drinks).
 * =================================================================================
 */

data class CartItem(val name: String, val price: Int, var quantity: Int, val imageUrl: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    // --- STATE MANAGEMENT ---
    val itemsInCart = remember { 
        mutableStateListOf(
            CartItem("Spicy Noodles", 1500, 2, "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400")
        )
    }

    // --- EXTENDED QUICK ADD DATABASE ---
    // Added all food, sides, and drinks as you requested!
    val allAdditions = listOf(
        // Meals
        CartItem("Jollof Rice", 2500, 1, "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=400"),
        CartItem("Grilled Salmon", 5000, 1, "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400"),
        CartItem("Pizza", 15000, 1, "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"),
        // Sides
        CartItem("French Fries", 500, 1, "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400"),
        CartItem("Coleslaw", 400, 1, "https://images.unsplash.com/photo-1546793665-c74683c3f43d?w=400"),
        CartItem("Garlic Bread", 800, 1, "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=400"),
        // Drinks
        CartItem("Coke", 300, 1, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400"),
        CartItem("Smoothie", 1500, 1, "https://images.unsplash.com/photo-1502741224143-90386d7f8c82?w=400"),
        CartItem("Water", 200, 1, "https://images.unsplash.com/photo-1523362628242-f513a5e3260a?w=400")
    )

    val subtotal = itemsInCart.sumOf { it.price * it.quantity }
    val total = subtotal + 500

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.padding(24.dp)) {
                    CartSummaryRow("Subtotal", "₦$subtotal")
                    CartSummaryRow("Delivery Fee", "₦500")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    CartSummaryRow("Total", "₦$total", isTotal = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCheckoutClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Place Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color(0xFFF8F8F8)).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- EXTENDED QUICK ADD SECTION ---
            item {
                Text("Complete your meal?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(allAdditions) { suggested ->
                        QuickAddCard(
                            item = suggested,
                            onAddClick = {
                                val existing = itemsInCart.find { it.name == suggested.name }
                                if (existing != null) {
                                    val index = itemsInCart.indexOf(existing)
                                    itemsInCart[index] = existing.copy(quantity = existing.quantity + 1)
                                } else {
                                    itemsInCart.add(suggested.copy())
                                }
                            }
                        )
                    }
                }
            }

            item { Text("Items in Basket", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black) }

            items(itemsInCart) { item ->
                CartItemCard(
                    item = item,
                    onIncrease = { 
                        val index = itemsInCart.indexOf(item)
                        itemsInCart[index] = item.copy(quantity = item.quantity + 1)
                    },
                    onDecrease = { 
                        if (item.quantity > 1) {
                            val index = itemsInCart.indexOf(item)
                            itemsInCart[index] = item.copy(quantity = item.quantity - 1)
                        }
                    },
                    onDelete = { itemsInCart.remove(item) }
                )
            }
        }
    }
}

/**
 * REUSABLE COMPONENTS WITH COMMENTS
 */
@Composable
fun QuickAddCard(item: CartItem, onAddClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.name, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
            Text("₦${item.price}", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddClick,
                modifier = Modifier.height(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00))
            ) {
                Text("Add", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text("₦${item.price}", color = Color(0xFFFF8C00), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp).background(Color(0xFFF0F0F0), CircleShape)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Black)
                    }
                    Text(text = item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold, color = Color.Black)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp).background(Color(0xFFFF8C00), CircleShape)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray) }
        }
    }
}

@Composable
fun CartSummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Black, fontSize = if (isTotal) 20.sp else 16.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(text = value, color = if (isTotal) Color.Black else Color.DarkGray, fontSize = if (isTotal) 20.sp else 16.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
    }
}
