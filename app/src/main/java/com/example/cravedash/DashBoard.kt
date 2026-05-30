package com.example.cravedash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Data models
// ─────────────────────────────────────────────────────────────────────────────
data class FoodItem(
    val name: String,
    val price: String,
    val category: String,
    val description: String = "",
    val imageUrl: String = ""
)

data class SpecialItem(
    val name: String,
    val salePrice: String,
    val originalPrice: String,
    val badge: String,
    val imageUrl: String
)

// allFoodItems and popularFoodItems have moved to MenuRepository.
// DashBoard now reads from MenuRepository.items (reactive, persisted).

// Auto-scrolling specials
val todaysSpecials = listOf(
    SpecialItem("Special Burger",  "₦1,800", "₦2,500", "28% OFF", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800"),
    SpecialItem("Jollof Rice",     "₦2,125", "₦2,500", "15% OFF", "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=800"),
    SpecialItem("Grilled Salmon",  "₦4,000", "₦5,000", "20% OFF", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=800"),
    SpecialItem("Pounded Yam",     "₦2,975", "₦3,500", "15% OFF", "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800"),
    SpecialItem("Spicy Noodles",   "₦1,350", "₦1,500",  "10% OFF", "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=800"),
)

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashBoard(
    onCategoryClick: (String) -> Unit = {},  // single dynamic callback replaces 4 hardcoded ones
    onChatClick:     () -> Unit,
    onItemClick:     (FoodItem) -> Unit,
    // Legacy aliases kept so existing call sites compile without changes:
    onMealClick:  () -> Unit = { onCategoryClick("Meals")    },
    onSideClick:  () -> Unit = { onCategoryClick("Sides")    },
    onSnackClick: () -> Unit = { onCategoryClick("Grills")   },
    onDrinkClick: () -> Unit = { onCategoryClick("Drinks")   }
) {
    val isDark   = ThemeManager.isDarkMode
    val bg       = MaterialTheme.colorScheme.background
    val surface  = MaterialTheme.colorScheme.surface
    val onBg     = MaterialTheme.colorScheme.onBackground
    val subtext  = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val divider  = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)

    var searchText by remember { mutableStateOf("") }

    // Today's Special — swipeable pager with auto-advance
    val specialPager = rememberPagerState { todaysSpecials.size }
    LaunchedEffect(specialPager.currentPage) {
        delay(3_500)
        val next = (specialPager.currentPage + 1) % todaysSpecials.size
        specialPager.animateScrollToPage(next)
    }

    val searchResults = if (searchText.isNotBlank())
        MenuRepository.items.filter { it.name.contains(searchText, ignoreCase = true) }
    else emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ────────────────────────────────────────────────────────
            Surface(color = surface, shadowElevation = 2.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Hello, ${UserSession.displayName} 👋",
                                color = Color(0xFFFF8C00),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "What are you craving?",
                                color = onBg,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF8C00)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                UserSession.firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search jollof rice, suya, burger…", color = subtext, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = subtext) },
                        trailingIcon = {
                            if (searchText.isNotEmpty())
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Close, null, tint = subtext)
                                }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFFFF8C00),
                            unfocusedBorderColor = divider,
                            focusedTextColor     = onBg,
                            unfocusedTextColor   = onBg,
                            focusedContainerColor   = surface,
                            unfocusedContainerColor = surface,
                            cursorColor          = Color(0xFFFF8C00)
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (searchText.isNotEmpty()) {
                // ── SEARCH RESULTS ─────────────────────────────────────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text("Results for \"$searchText\"", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = onBg)
                    Spacer(Modifier.height(12.dp))
                    if (searchResults.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text("No items found 😕", color = subtext, fontSize = 15.sp)
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(searchResults) { food -> DashFoodCard(food, onBg, surface, isDark) { onItemClick(food) } }
                        }
                    }
                }
            } else {

                // ── CATEGORIES — dynamic, driven by MenuRepository ─────────────
                Surface(color = surface, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        DashSectionTitle("Categories", onBg,
                            modifier = Modifier.padding(horizontal = 20.dp))
                        Spacer(Modifier.height(14.dp))
                        LazyRow(
                            contentPadding        = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(MenuRepository.categories) { category ->
                                RealCategoryCard(
                                    name     = category,
                                    imageUrl = categoryImages[category]
                                        ?: "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=300",
                                    onClick  = { onCategoryClick(category) }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── TODAY'S SPECIAL — swipeable + Order Now works ──────────────
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashSectionTitle("Today's Special 🔥", onBg)
                        // Animated dot indicators
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            todaysSpecials.forEachIndexed { i, _ ->
                                val isActive = i == specialPager.currentPage
                                val dotWidth by animateDpAsState(
                                    targetValue   = if (isActive) 20.dp else 6.dp,
                                    animationSpec = tween(300),
                                    label         = "dot$i"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(dotWidth, 6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (isActive) Color(0xFFFF8C00) else divider
                                        )
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    // HorizontalPager — manual swipe + auto-advance
                    HorizontalPager(
                        state    = specialPager,
                        modifier = Modifier.fillMaxWidth()
                    ) { idx ->
                        val s = todaysSpecials[idx]
                        // Find matching FoodItem for navigation
                        val foodItem = MenuRepository.items.find { it.name == s.name }
                            ?: FoodItem(s.name, s.salePrice, "Meals", "Today's special deal.", s.imageUrl)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onItemClick(foodItem) }  // tap card → food details
                        ) {
                            AsyncImage(
                                model              = s.imageUrl,
                                contentDescription = s.name,
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier.fillMaxSize().background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Black.copy(0.82f), Color.Transparent)
                                    )
                                )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 20.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFFF8C00),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        s.badge,
                                        modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        color      = Color.White,
                                        fontSize   = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(s.name,
                                    color      = Color.White,
                                    fontSize   = 20.sp,
                                    fontWeight = FontWeight.ExtraBold)
                                Row(
                                    verticalAlignment    = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(s.salePrice,
                                        color      = Color(0xFFFF8C00),
                                        fontSize   = 16.sp,
                                        fontWeight = FontWeight.ExtraBold)
                                    Text(s.originalPrice,
                                        color = Color.White.copy(0.5f),
                                        fontSize = 12.sp,
                                        style = androidx.compose.ui.text.TextStyle(
                                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                        ))
                                }
                                Spacer(Modifier.height(10.dp))
                                // Order Now — now actually works
                                Surface(
                                    color    = Color.White,
                                    shape    = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        val priceInt = s.salePrice
                                            .replace("₦","").replace(",","").trim()
                                            .toIntOrNull() ?: 0
                                        CartManager.add(s.name, priceInt, s.imageUrl)
                                        onItemClick(foodItem)
                                    }
                                ) {
                                    Text(
                                        "Order Now",
                                        modifier   = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                        color      = Color(0xFFFF8C00),
                                        fontSize   = 12.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── POPULAR NOW ─────────────────────────────────────────────────
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashSectionTitle("Popular Now 🇳🇬", onBg)
                        Text("See all", color = Color(0xFFFF8C00), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onMealClick() })
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(MenuRepository.popular) { food ->
                            DashFoodCard(food, onBg, surface, isDark) { onItemClick(food) }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── ALL ITEMS ───────────────────────────────────────────────────
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DashSectionTitle("All Items", onBg)
                        Text("See all", color = Color(0xFFFF8C00), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onMealClick() })
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(MenuRepository.items.take(12)) { food ->
                            DashFoodCard(food, onBg, surface, isDark) { onItemClick(food) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        // ── FLOATING MAX BUTTON ─────────────────────────────────────────────────
        FloatingActionButton(
            onClick = onChatClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).shadow(12.dp, CircleShape),
            containerColor = Color(0xFFFF8C00),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.SupportAgent, contentDescription = "Chat with MAX", modifier = Modifier.size(26.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Real photo category card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun RealCategoryCard(name: String, imageUrl: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.18f))
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Food card — rectangular image, consistent with FoodDetails navigation
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DashFoodCard(
    food: FoodItem,
    onBg: Color,
    surface: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.width(155.dp).clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 3.dp),
        border    = if (isDark) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)) else null
    ) {
        Column {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFEEEEEE))
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    color = onBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(food.price, color = Color(0xFFFF8C00), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF8C00)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DashSectionTitle(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = color, modifier = modifier)
}

// Legacy compat shims
@Composable
fun FoodItemCard(name: String, price: String, imageUrl: String, onClick: () -> Unit) {
    val isDark = ThemeManager.isDarkMode
    DashFoodCard(
        food = FoodItem(name = name, price = price, category = "", imageUrl = imageUrl),
        onBg = MaterialTheme.colorScheme.onBackground,
        surface = MaterialTheme.colorScheme.surface,
        isDark = isDark,
        onClick = onClick
    )
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun CategoryCard(name: String, emoji: String, bgColor: Color, onClick: () -> Unit) {
    RealCategoryCard(name = name, imageUrl = "", onClick = onClick)
}

@Composable
fun CategoryItem(name: String, emoji: String, onClick: () -> Unit) {
    RealCategoryCard(name = name, imageUrl = "", onClick = onClick)
}
