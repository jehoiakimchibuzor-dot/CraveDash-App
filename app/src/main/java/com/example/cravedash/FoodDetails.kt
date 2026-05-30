package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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
    val isDark   = ThemeManager.isDarkMode
    val bg       = MaterialTheme.colorScheme.background
    val surface  = MaterialTheme.colorScheme.surface
    val onBg     = MaterialTheme.colorScheme.onBackground
    val sub      = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val divider  = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
    val qtyBg    = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val qtyText  = if (isDark) Color.White else Color(0xFF1A1A1A)

    var quantity by remember { mutableStateOf(1) }
    val priceNumber = itemPrice.replace("₦", "").replace(",", "").trim().toIntOrNull() ?: 0
    val totalPrice  = "₦${"%,d".format(priceNumber * quantity)}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                // Semi-transparent so it floats over the hero image
                                .background(surface.copy(alpha = 0.88f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = onBg)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(surface.copy(alpha = 0.88f)),
                            contentAlignment = Alignment.Center
                        ) {
                            BadgedBox(
                                badge = {
                                    if (CartManager.count > 0) {
                                        Badge(containerColor = Color(0xFFFF8C00)) {
                                            Text(CartManager.count.toString(),
                                                color    = Color.White,
                                                fontSize = 9.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint     = onBg,
                                    modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            // ── Add to Cart bar — fully theme-aware ──────────────────────────
            Surface(
                modifier        = Modifier.fillMaxWidth(),
                shadowElevation = 20.dp,
                color           = surface          // ← was hardcoded Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ── Quantity selector ─────────────────────────────────────
                    Row(
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(qtyBg)     // ← was hardcoded Color(0xFFF5F5F5)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        IconButton(
                            onClick  = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove,
                                contentDescription = "Less",
                                tint     = if (quantity > 1) Color(0xFFFF8C00) else sub,
                                modifier = Modifier.size(18.dp))
                        }
                        Text(
                            "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp,
                            color      = qtyText   // ← was hardcoded Color.Black
                        )
                        IconButton(
                            onClick  = { quantity++ },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add,
                                contentDescription = "More",
                                tint     = Color(0xFFFF8C00),
                                modifier = Modifier.size(18.dp))
                        }
                    }

                    // ── Add to Cart button ────────────────────────────────────
                    Button(
                        onClick = {
                            val decoded = try {
                                java.net.URLDecoder.decode(itemImage, "UTF-8")
                            } catch (_: Exception) { itemImage }
                            repeat(quantity) { CartManager.add(itemName, priceNumber, decoded) }
                            onCartClick()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape    = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            "Add to Cart  •  $totalPrice",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }
            }
        },
        containerColor = bg    // ← was hardcoded Color(0xFFF7F7F7)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HERO IMAGE ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model              = itemImage,
                    contentDescription = itemName,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxSize()
                        .background(if (isDark) Color(0xFF2A2A2A) else Color(0xFFEEEEEE))
                )
                // Gradient blends hero into background — theme-aware
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, bg))
                        )
                )
            }

            // ── CONTENT CARD ──────────────────────────────────────────────────
            Surface(
                modifier        = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-20).dp),
                color           = surface,    // ← was hardcoded Color.White
                shape           = RoundedCornerShape(20.dp),
                shadowElevation = if (isDark) 0.dp else 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Name + price
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            itemName,
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = onBg,   // ← was Color.Black
                            modifier   = Modifier.weight(1f)
                        )
                        Text(
                            itemPrice,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color(0xFFFF8C00)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Rating row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { i ->
                            Icon(Icons.Default.Star,
                                contentDescription = null,
                                tint     = if (i < 4) Color(0xFFFFB300) else sub,
                                modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                        Text("4.0", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = onBg)
                        Spacer(Modifier.width(4.dp))
                        Text("(128 reviews)", fontSize = 12.sp, color = sub)
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = divider)
                    Spacer(Modifier.height(16.dp))

                    // Info chips
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoChip("⏱ 15–20 min", isDark)
                        InfoChip("🔥 Prep: Fresh", isDark)
                        InfoChip("📦 XL Portion", isDark)
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = divider)
                    Spacer(Modifier.height(16.dp))

                    // Description
                    Text("About this dish",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = onBg)     // ← was Color.Black
                    Spacer(Modifier.height(8.dp))
                    Text(
                        description.ifBlank { "A delicious meal prepared fresh with the finest locally sourced ingredients." },
                        fontSize   = 14.sp,
                        color      = sub,      // ← was Color.DarkGray
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = divider)
                    Spacer(Modifier.height(16.dp))

                    // Details
                    Text("Details",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        color      = onBg)
                    Spacer(Modifier.height(8.dp))

                    listOf(
                        "🥘" to "Prepared fresh using locally sourced ingredients",
                        "🌿" to "No artificial preservatives",
                        "⚠️" to "May contain dairy and traces of nuts",
                        "♻️" to "Packaged in eco-friendly, heat-insulated containers"
                    ).forEach { (icon, text) ->
                        Row(
                            modifier          = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(icon, fontSize = 14.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(text,
                                fontSize   = 13.sp,
                                color      = sub,    // ← was Color.DarkGray
                                lineHeight = 20.sp,
                                modifier   = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoChip(text: String, isDark: Boolean) {
    Surface(
        color = if (isDark) Color(0xFF2A2000) else Color(0xFFFFF3E0),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize   = 11.sp,
            color      = if (isDark) Color(0xFFFFB347) else Color(0xFFE65100),
            fontWeight = FontWeight.Medium
        )
    }
}
