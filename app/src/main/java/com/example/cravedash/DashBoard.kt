package com.example.cravedash

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SupportAgent
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

// =================================================================================
// Data Blueprint for a Food Item on the Dashboard
// =================================================================================
data class FoodItem(val name: String, val price: String, val category: String, val description: String = "", val imageUrl: String = "")

// --- OUR DASHBOARD DATABASE (Full 12 Items Restored) ---
val allFoodItems = listOf(
    FoodItem("Beef Salad", "₦4,200", "Meals", "Fresh beef strips over crisp greens.", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400"),
    FoodItem("Spicy Noodles", "₦5,500", "Meals", "Fiery stir-fried noodles with veggies.", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400"),
    FoodItem("Sushi", "₦18,000", "Meals", "Fresh salmon and avocado rolls.", "https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400"),
    FoodItem("Pizza", "₦15,000", "Meals", "Cheesy pepperoni pizza with thin crust.", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=400"),
    FoodItem("Spaghetti", "₦12,000", "Meals", "Classic Italian pasta with rich meat sauce.", "https://images.unsplash.com/photo-1516100882582-96c3a05fe590?w=400"),
    FoodItem("Jollof Rice", "₦3,500", "Meals", "Smoky, spicy West African rice bowl.", "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400"),
    FoodItem("French Fries", "₦2,500", "Sides", "Crispy, golden-brown potato strips.", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400"),
    FoodItem("Coleslaw", "₦1,500", "Sides", "Crunchy shredded cabbage and carrots.", "https://images.unsplash.com/photo-1572449043416-55f4685c9bb7?w=400"),
    FoodItem("Spring Rolls", "₦2,000", "Snacks", "Crispy fried vegetable spring rolls.", "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=400"),
    FoodItem("Meat Pie", "₦1,800", "Snacks", "Buttery crust filled with minced meat.", "https://images.unsplash.com/photo-1601050633729-195085878d96?w=400"),
    FoodItem("Coca-Cola", "₦500", "Drinks", "Chilled classic sparkling cola drink.", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400"),
    FoodItem("Water", "₦300", "Drinks", "Pure refreshing bottled spring water.", "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=400")
)

// Picking some trending items for the "Popular Now" section
val popularFoodItems = listOf(
    allFoodItems[1], // Spicy Noodles
    allFoodItems[3], // Pizza
    allFoodItems[6], // French Fries
    allFoodItems[9], // Meat Pie
)

@Composable
fun DashBoard(
    onMealClick: () -> Unit,
    onSideClick: () -> Unit,
    onSnackClick: () -> Unit,
    onDrinkClick: () -> Unit,
    onChatClick: () -> Unit, // NEW: Parameter to handle chat button click
    onItemClick: (FoodItem) -> Unit 
) {
    // --- STATE MANAGEMENT ---
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Meals") }

    // --- SEARCH FILTER LOGIC ---
    val searchResults = allFoodItems.filter { 
        it.name.contains(searchText, ignoreCase = true) 
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .safeDrawingPadding() 
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), 
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Greeting text
            Text("Welcome!", color = Color(0xFFFF8C00), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("What would you like to eat?", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- THE SEARCH BAR ---
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it }, 
                placeholder = { Text("Search your favorite food...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = Color.Gray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- DYNAMIC CONTENT ---
            if (searchText.isNotEmpty()) {
                // --- SEARCH RESULTS MODE ---
                Text("Search Results", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(searchResults) { foodItem ->
                        FoodItemCard(
                            name = foodItem.name, 
                            price = foodItem.price,
                            imageUrl = foodItem.imageUrl,
                            onClick = { onItemClick(foodItem) }
                        )
                    }
                }
                
                if (searchResults.isEmpty()) {
                    Text("No items found for \"$searchText\"", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                }
            } else {
                // --- NORMAL DASHBOARD MODE ---
                
                // 1. Categories Header
                Text(
                    text = "Categories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black 
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    CategoryItem("Meals", "🍔", onMealClick)
                    CategoryItem("Sides", "🍟", onSideClick)
                    CategoryItem("Snacks", "🍩", onSnackClick)
                    CategoryItem("Drinks", "🥤", onDrinkClick)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 2. Today's Special Offer
                Text("Today's Special Offer", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=400",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.DarkGray)
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text("CraveDash Special Burger", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Now ₦1,800 (10% off)", color = Color.White)
                            Spacer(modifier = Modifier.weight(1f))
                            Button(
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFFF8C00)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("Add to Cart", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Popular Now
                Text("Popular Now", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(popularFoodItems) { foodItem ->
                        FoodItemCard(
                            name = foodItem.name, 
                            price = foodItem.price,
                            imageUrl = foodItem.imageUrl,
                            onClick = { onItemClick(foodItem) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- FLOATING CHAT BUTTON ---
        // I added this floating action button to make it easy to access the chat bot from anywhere on the dashboard.
        LargeFloatingActionButton(
            onClick = onChatClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFFFF8C00),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = "Chat with MAX",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Reusable component for category circles with emojis
 */
@Composable
fun CategoryItem(name: String, emoji: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)), 
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp)
        }
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black 
        )
    }
}

// Reusable component for food cards
@Composable
fun FoodItemCard(name: String, price: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray)
            )
            Spacer(Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.SemiBold, color = Color.Black, maxLines = 1)
            Text(price, color = Color(0xFFFF8C00), fontWeight = FontWeight.Bold)
        }
    }
}
