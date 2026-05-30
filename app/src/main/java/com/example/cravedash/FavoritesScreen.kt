package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onItemClick: (MenuItem) -> Unit
) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val card    = if (isDark) Color(0xFF252525) else Color.White

    // Read directly from the global FavoritesManager — shared with MenuScreen
    val favoriteItems = FavoritesManager.items

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorites", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg) } },
                actions = {
                    if (favoriteItems.isNotEmpty()) {
                        IconButton(onClick = { FavoritesManager.clear() }) { Icon(Icons.Default.DeleteSweep, null, tint = sub) }
                    }
                    IconButton(onClick = onCartClick) {
                        BadgedBox(badge = {
                            if (CartManager.count > 0) {
                                Badge(containerColor = Color(0xFFFF8C00)) {
                                    Text(CartManager.count.toString(), color = Color.White)
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
        containerColor = bg
    ) { padding ->
        if (favoriteItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(90.dp), tint = sub)
                    Spacer(Modifier.height(14.dp))
                    Text("Your Crave List is empty", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = onBg)
                    Text("Tap ♡ on any meal to save it here!", textAlign = TextAlign.Center, color = sub, modifier = Modifier.padding(16.dp))
                    Button(onClick = onBackClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)), shape = RoundedCornerShape(12.dp)) {
                        Text("Explore Menu")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(favoriteItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onItemClick(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = card),
                        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 3.dp)
                    ) {
                        Column {
                            Box {
                                AsyncImage(
                                    model = item.imageModel,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(120.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                        .background(sub.copy(0.1f))
                                )
                                IconButton(
                                    onClick = { FavoritesManager.remove(item.name) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                                ) {
                                    Icon(Icons.Default.Favorite, null, tint = Color(0xFFFF8C00), modifier = Modifier.size(20.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(item.name, fontWeight = FontWeight.Bold, color = onBg, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(item.price, color = Color(0xFFFF8C00), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
