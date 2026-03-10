package com.example.cravedash

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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * =================================================================================
 * FAVORITES SCREEN: The user's personal "Crave List".
 * This screen displays all items the user has marked as favorites for quick access.
 * It uses a reactive list to allow instant removal of items.
 * =================================================================================
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit,      // Function to go back to the previous screen
    onCartClick: () -> Unit,      // Function to open the shopping cart
    onItemClick: (MenuItem) -> Unit // Function to view the full details of a food item
) {
    // --- STATE MANAGEMENT ---
    // 'mutableStateListOf' is the "Brain" of this screen. 
    // In a real app, this would be connected to a database (like Firebase or Room).
    // For this MVP, we initialize it with items the user has already "Liked".
    val favoriteItems = remember {
        mutableStateListOf(
            MenuItem("Spicy Noodles", "₦1,500", "Fiery stir-fried noodles with veggies.", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400"),
            MenuItem("Grilled Salmon", "₦5,000", "Pan-seared Atlantic salmon fillet.", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400"),
            MenuItem("Jollof Rice", "₦2,500", "Smoky, spicy West African rice bowl.", "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=400"),
            MenuItem("Chicken Wings", "₦1,500", "Six pieces of crispy glazed wings.", "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400")
        )
    }

    Scaffold(
        // ============================ TOP NAVIGATION BAR ============================
        topBar = {
            TopAppBar(
                title = { Text("My Favorites", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    // --- CLEAR ALL BUTTON ---
                    // A professional feature allowing users to reset their list in one tap.
                    if (favoriteItems.isNotEmpty()) {
                        IconButton(onClick = { favoriteItems.clear() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All", tint = Color.Gray)
                        }
                    }
                    
                    // --- CART BUTTON ---
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = Color(0xFFFF8C00)) {
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
        containerColor = Color(0xFFF8F8F8) // Premium light grey background to make cards "Pop"
    ) { innerPadding ->
        
        // ============================ MAIN CONTENT AREA ============================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Check if the list is empty to show either the Grid or the Empty State
            if (favoriteItems.isEmpty()) {
                
                // --- EMPTY STATE VIEW ---
                // This UI appears when the user has unfollowed everything.
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder, 
                            contentDescription = null, 
                            modifier = Modifier.size(100.dp), 
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your Crave List is empty", 
                            fontSize = 22.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.Black
                        )
                        Text(
                            text = "Tap the heart icon on any meal to save it here!", 
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        // Nudge button to go back to shopping
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Go Explore Menu")
                        }
                    }
                }
            } else {
                
                // --- FAVORITES GRID ---
                // Displays items in a sleek 2-column scrollable grid.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Loop through the reactive 'favoriteItems' list
                    items(favoriteItems) { item ->
                        FavoriteItemCard(
                            item = item,
                            // Dynamic removal: clicking the heart immediately removes it from the screen
                            onRemoveClick = { favoriteItems.remove(item) },
                            onClick = { onItemClick(item) } 
                        )
                    }
                }
            }
        }
    }
}

/**
 * REUSABLE COMPONENT: A sleek card specifically designed for Favorites.
 * Features: High-quality image, price, name, and a toggle-able heart icon.
 */
@Composable
fun FavoriteItemCard(
    item: MenuItem,
    onRemoveClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp), // Modern, soft rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- PRODUCT IMAGE ---
                // Grabs the photo from the web link.
                AsyncImage(
                    model = item.imageModel,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape) // Standard "Geek/Pro" circular mask
                        .background(Color.LightGray)
                )
                
                Spacer(Modifier.height(12.dp))
                
                // --- ITEM INFO ---
                Text(
                    text = item.name, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black, 
                    fontSize = 16.sp, 
                    maxLines = 1 // Prevents long names from breaking the design
                )
                
                Text(
                    text = item.price, 
                    color = Color(0xFFFF8C00), // Signature branding orange
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 15.sp
                )
            }

            // --- THE UNFAVORITE BUTTON ---
            // Positioned in the top-right corner using the 'Box' alignment.
            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                // Using the filled heart icon to show it is currently a favorite
                Icon(
                    imageVector = Icons.Default.Favorite, 
                    contentDescription = "Remove from Favorites", 
                    tint = Color(0xFFFF8C00)
                )
            }
        }
    }
}
