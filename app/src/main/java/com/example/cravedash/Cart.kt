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
import androidx.compose.material.icons.filled.*
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

data class CartItem(val name: String, val price: Int, var quantity: Int, val imageUrl: String = "")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onBackClick: () -> Unit, onCheckoutClick: () -> Unit) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val card    = if (isDark) Color(0xFF252525) else Color.White
    val divider = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)

    // Cart reads from global CartManager — items added from FoodDetails persist here
    val itemsInCart = CartManager.items
    val additions = listOf(
        CartItem("Suya",          2000, 1, "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=400"),
        CartItem("Pounded Yam",   3500, 1, "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400"),
        CartItem("French Fries",   500, 1, "https://images.unsplash.com/photo-1576107232684-1279f8c7bc5b?w=400"),
        CartItem("Fried Plantain", 500, 1, "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=400"),
        CartItem("Chapman",       1200, 1, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400"),
        CartItem("Zobo Drink",     400, 1, "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=400"),
        CartItem("Coke",           300, 1, "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=400"),
        CartItem("Smoothie",      1500, 1, "https://images.unsplash.com/photo-1502741224143-90386d7f8c82?w=400")
    )
    val subtotal = CartManager.subtotal
    val total    = CartManager.total

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        bottomBar = {
            // Only show the checkout bar when there are items
            if (itemsInCart.isNotEmpty()) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = surface) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        CartRow("Subtotal", "₦$subtotal", onBg, sub)
                        CartRow("Delivery Fee", "₦500", onBg, sub)
                        Divider(color = divider, modifier = Modifier.padding(vertical = 8.dp))
                        CartRow("Total", "₦$total", onBg, sub, isTotal = true)
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = onCheckoutClick,
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Place Order", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold) }
                    }
                }
            }
        },
        containerColor = bg
    ) { padding ->

        // ── Empty cart state ──────────────────────────────────────────────────
        if (itemsInCart.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("🛒", fontSize = 72.sp)
                    Spacer(Modifier.height(20.dp))
                    Text("Your cart is empty",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = onBg)
                    Spacer(Modifier.height(8.dp))
                    Text("Add something delicious from the menu!",
                        fontSize = 14.sp,
                        color = sub,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(50.dp).fillMaxWidth()
                    ) {
                        Text("Browse Menu  🍛",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White)
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Complete your meal?", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = onBg)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(additions) { a ->
                        QuickAddCard(a, card, onBg, sub) {
                            CartManager.add(a.name, a.price, a.imageUrl)
                        }
                    }
                }
            }
            item { Text("Items in Basket", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = onBg) }
            items(itemsInCart.toList()) { item ->
                CartItemCard(item, card, onBg, sub,
                    onIncrease = { CartManager.increase(item) },
                    onDecrease = { CartManager.decrease(item) },
                    onDelete   = { CartManager.remove(item) }
                )
            }
        }
    }
}

@Composable
fun QuickAddCard(item: CartItem, card: Color, onBg: Color, sub: Color, onAddClick: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = card),
        border = BorderStroke(1.dp, sub.copy(0.2f))) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(sub.copy(0.1f)))
            Spacer(Modifier.height(6.dp))
            Text(item.name, fontWeight = FontWeight.Bold, color = onBg, fontSize = 13.sp)
            Text("₦${item.price}", color = Color(0xFFFF8C00), fontSize = 11.sp)
            Spacer(Modifier.height(6.dp))
            Button(onClick = onAddClick, modifier = Modifier.height(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) { Text("Add", fontSize = 11.sp) }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, card: Color, onBg: Color, sub: Color, onIncrease: () -> Unit, onDecrease: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = card), shape = RoundedCornerShape(14.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.imageUrl, contentDescription = null, contentScale = ContentScale.Crop,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(sub.copy(0.1f)))
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = onBg)
                Text("₦${item.price}", color = Color(0xFFFF8C00), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp).background(sub.copy(0.15f), CircleShape)) {
                        Icon(Icons.Default.Remove, null, tint = onBg, modifier = Modifier.size(14.dp))
                    }
                    Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold, color = onBg)
                    IconButton(onClick = onIncrease, modifier = Modifier.size(30.dp).background(Color(0xFFFF8C00), CircleShape)) {
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = sub) }
        }
    }
}

@Composable
fun CartRow(label: String, value: String, onBg: Color, sub: Color, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = if (isTotal) onBg else sub, fontSize = if (isTotal) 18.sp else 15.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = if (isTotal) Color(0xFFFF8C00) else onBg, fontSize = if (isTotal) 18.sp else 15.sp, fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Normal)
    }
}

// Legacy compat
@Composable fun CartSummaryRow(label: String, value: String, isTotal: Boolean = false) = CartRow(label, value, MaterialTheme.colorScheme.onBackground, Color(0xFF888888), isTotal)
