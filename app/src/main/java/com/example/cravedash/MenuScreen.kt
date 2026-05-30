package com.example.cravedash

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class MenuItem(val name: String, val price: String, val description: String, val imageModel: Any)
data class BottomNavItem(val label: String, val icon: ImageVector, val action: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onLiveChatClick: () -> Unit,
    onItemClick: (MenuItem) -> Unit,
    initialCategory: String = "Meals"
) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val divider = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var searchText       by remember { mutableStateOf("") }
    // Favorites now backed by global FavoritesManager — state persists across screens

    // Derived from MenuRepository — auto-updates when admin adds/edits/deletes items
    // Categories are dynamic: whatever exists in the repository shows as a tab
    val allItems by remember {
        derivedStateOf {
            MenuRepository.categories.associateWith { cat ->
                MenuRepository.byCategory(cat).map { it.toMenuItem() }
            }
        }
    }

    val foodItems = (allItems[selectedCategory] ?: emptyList()).filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Our Menu", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = onBg) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg)
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(badge = {
                            if (CartManager.count > 0) {
                                Badge(containerColor = Color(0xFFFF8C00)) {
                                    Text(CartManager.count.toString(), color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, null, tint = onBg)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        // Bottom nav is handled by the app shell (MainActivity) — no duplicate here
        containerColor = bg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search + category tabs header
            Surface(color = surface, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search dishes…", color = sub) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = sub) },
                        trailingIcon = {
                            if (searchText.isNotEmpty())
                                IconButton(onClick = { searchText = "" }) {
                                    Icon(Icons.Default.Close, null, tint = sub)
                                }
                        },
                        shape = RoundedCornerShape(14.dp),
                        textStyle = TextStyle(color = onBg),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = Color(0xFFFF8C00),
                            unfocusedBorderColor    = divider,
                            focusedContainerColor   = bg,
                            unfocusedContainerColor = bg,
                            cursorColor             = Color(0xFFFF8C00)
                        )
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MenuRepository.categories.forEach { cat ->
                            val sel = cat == selectedCategory
                            Surface(
                                onClick = { selectedCategory = cat },
                                shape   = RoundedCornerShape(20.dp),
                                color   = if (sel) Color(0xFFFF8C00) else Color.Transparent,
                                border  = if (!sel) BorderStroke(1.dp, divider) else null
                            ) {
                                Text(
                                    cat,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (sel) Color.White else sub,
                                    fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            if (foodItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items found 😕", color = sub, fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(foodItems) { item ->
                        MenuFoodCard(
                            menuItem        = item,
                            isFavorited     = FavoritesManager.isFavorite(item.name),
                            isDark          = isDark,
                            onBg            = onBg,
                            surface         = surface,
                            sub             = sub,
                            onFavoriteClick = { FavoritesManager.toggle(item) },
                            onClick         = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MenuFoodCard(
    menuItem: MenuItem,
    isFavorited: Boolean,
    isDark: Boolean,
    onBg: Color,
    surface: Color,
    sub: Color,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = surface),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 3.dp),
        border    = if (isDark) BorderStroke(1.dp, Color(0xFF333333)) else null
    ) {
        Column {
            Box {
                AsyncImage(
                    model = menuItem.imageModel,
                    contentDescription = menuItem.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color(0xFFEEEEEE)),
                    error = rememberVectorPainter(Icons.Default.BrokenImage)
                )
                // Heart
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(surface.copy(alpha = 0.90f))
                        .clickable(onClick = onFavoriteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (isFavorited) Color(0xFFFF8C00) else sub,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(menuItem.name, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    color = onBg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(menuItem.description, fontSize = 11.sp, color = sub,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(menuItem.price, color = Color(0xFFFF8C00), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Surface(color = Color(0xFFFF8C00), shape = RoundedCornerShape(8.dp)) {
                        Text("Add", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Legacy alias
@Composable
fun FoodItemCard(
    menuItem: MenuItem,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) = MenuFoodCard(menuItem, isFavorited, ThemeManager.isDarkMode,
    MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.surface,
    Color(0xFF888888), onFavoriteClick, onClick)
