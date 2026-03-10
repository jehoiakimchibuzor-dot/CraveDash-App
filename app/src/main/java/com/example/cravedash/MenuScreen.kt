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
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * =================================================================================
 * MENU SCREEN: The main digital catalog.
 * Updated: The Heart icon now toggles between Favorite and Unfavorite live!
 * =================================================================================
 */

// Blueprint for our food items.
data class MenuItem(val name: String, val price: String, val description: String, val imageModel: Any)

// Blueprint for the bottom navigation buttons
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
    // --- STATE MANAGEMENT ---
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var searchText by remember { mutableStateOf("") }
    
    // --- NEW: FAVORITES STATE ---
    // This 'set' tracks the names of all items the user has liked.
    // When a name is in this set, the heart turns orange and filled.
    var favoritedItemNames by remember { mutableStateOf(setOf<String>()) }

    // --- THE FULL 40-ITEM DATABASE ---
    val allItems = mapOf(
        "Meals" to listOf(
            MenuItem("Spicy Noodles", "₦1,500", "Fiery stir-fried noodles with veggies.", "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=400"),
            MenuItem("Shrimp Pasta", "₦1,800", "Creamy alfredo pasta with succulent shrimp.", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400"),
            MenuItem("Vegetable Curry", "₦1,200", "Hearty bowl of mixed vegetables in curry.", "https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=400"),
            MenuItem("Mixed Salad", "₦1,500", "Fresh garden greens with zesty lemon dressing.", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400"),
            MenuItem("Chicken Pasta", "₦1,500", "Pasta with grilled chicken chunks.", "https://images.unsplash.com/photo-1473093226795-af9932fe5856?w=400"),
            MenuItem("Beef Salad", "₦1,200", "Beef strips over crisp lettuce.", "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400"),
            MenuItem("Jollof Rice", "₦2,500", "Smoky, spicy West African rice bowl.", "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=400"),
            MenuItem("Fried Rice", "₦2,500", "Savory rice stir-fried with colorful vegetables.", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400"),
            MenuItem("Pounded Yam", "₦3,500", "Soft pounded yam with rich egusi soup.", "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400"),
            MenuItem("Grilled Salmon", "₦5,000", "Pan-seared Atlantic salmon fillet.", "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=400")
        ),
        "Sides" to listOf(
            MenuItem("French Fries", "₦500", "Crispy, golden-brown potato strips.", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=400"),
            MenuItem("Onion Rings", "₦600", "Deep-fried battered onion rings.", "https://images.unsplash.com/photo-1639024471283-03518883512d?w=400"),
            MenuItem("Coleslaw", "₦400", "Crunchy shredded cabbage and carrots.", "https://images.unsplash.com/photo-1546793665-c74683c3f43d?w=400"),
            MenuItem("Garlic Bread", "₦800", "Buttery toasted garlic bread slices.", "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=400"),
            MenuItem("Mashed Potatoes", "₦1,000", "Creamy, buttery whipped potatoes.", "https://images.unsplash.com/photo-1534939561126-855b8675edd7?w=400"),
            MenuItem("Steamed Veggies", "₦700", "A healthy mix of seasonal vegetables.", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400"),
            MenuItem("Fried Plantain", "₦500", "Sweet caramelized golden plantains.", "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=400"),
            MenuItem("Rice & Peas", "₦900", "Traditional coconut rice with kidney beans.", "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400"),
            MenuItem("Corn on Cob", "₦400", "Sweet buttered grilled corn on the cob.", "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=400"),
            MenuItem("Side Salad", "₦600", "A small portion of fresh garden greens.", "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400")
        ),
        "Snacks" to listOf(
            MenuItem("Chocolate Cake", "₦800", "Rich moist dark chocolate cake.", "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400"),
            MenuItem("Ice Cream", "₦700", "Three scoops of creamy assorted ice cream.", "https://images.unsplash.com/photo-1567206563064-6f60f40a2b57?w=400"),
            MenuItem("Cookies", "₦500", "Warm, chewy chocolate chip cookies.", "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400"),
            MenuItem("Popcorn", "₦400", "Light and fluffy salted movie popcorn.", "https://images.unsplash.com/photo-1585647347384-2593bc35786b?w=400"),
            MenuItem("Sausage Roll", "₦300", "Savory sausage meat in flaky golden pastry.", "https://images.unsplash.com/photo-1623334044303-241021148843?w=400"),
            MenuItem("Meat Pie", "₦500", "Buttery crust filled with minced meat.", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400"),
            MenuItem("Chicken Wings", "₦1,500", "Six pieces of crispy glazed wings.", "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400"),
            MenuItem("Spring Rolls", "₦600", "Crispy fried vegetable spring rolls.", "https://images.unsplash.com/photo-1544025162-d76694265947?w=400"),
            MenuItem("Potato Chips", "₦300", "Crunchy salted potato chips.", "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400"),
            MenuItem("Donuts", "₦400", "Soft glazed breakfast donuts.", "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=400")
        ),
        "Drinks" to listOf(
            MenuItem("Water", "₦200", "Pure refreshing bottled spring water.", "https://images.unsplash.com/photo-1523362628242-f513a5e3260a?w=400"),
            MenuItem("Coke", "₦300", "Chilled classic sparkling cola drink.", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400"),
            MenuItem("Juice", "₦400", "Freshly squeezed seasonal fruit juice.", "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=400"),
            MenuItem("Fanta", "₦300", "Bright and bubbly orange-flavored soda.", "https://images.unsplash.com/photo-1624517452488-04869289c4ca?w=400"),
            MenuItem("Sprite", "₦300", "Crisp lemon-lime flavored soda.", "https://images.unsplash.com/photo-1625772290748-39093c02b33e?w=400"),
            MenuItem("Zobo Drink", "₦400", "Traditional hibiscus beverage with ginger.", "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=400"),
            MenuItem("Chapman", "₦1,200", "Famous Nigerian fruity mocktail blend.", "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400"),
            MenuItem("Smoothie", "₦1,500", "Thick blend of fresh seasonal fruits.", "https://images.unsplash.com/photo-1502741224143-90386d7f8c82?w=400"),
            MenuItem("Milkshake", "₦1,800", "Creamy blend of milk and ice cream.", "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=400"),
            MenuItem("Hot Chocolate", "₦800", "Warm comforting cocoa with milk.", "https://images.unsplash.com/photo-1544787210-229f05bc3836?w=400")
        )
    )

    // --- SEARCH & FILTER LOGIC ---
    val foodItems = (allItems[selectedCategory] ?: emptyList()).filter { 
        it.name.contains(searchText, ignoreCase = true) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Our Menu", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
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
        bottomBar = {
            val navItems = listOf(
                BottomNavItem("Live Chat", Icons.AutoMirrored.Filled.Chat, onLiveChatClick),
                BottomNavItem("Profile", Icons.Default.Person, onProfileClick),
                BottomNavItem("Home", Icons.Default.Home, onHomeClick),
                BottomNavItem("Menu", Icons.Default.RestaurantMenu, onMenuClick),
                BottomNavItem("Favorites", Icons.Default.Favorite, onFavoriteClick)
            )

            NavigationBar(containerColor = Color.White) {
                navItems.forEach { item ->
                    val isSelected = item.label == "Menu"
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = item.action,
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF8C00),
                            selectedTextColor = Color(0xFFFF8C00),
                            indicatorColor = Color(0xFFFFE0B2),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding -> 

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search for dishes...") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            val categories = listOf("Meals", "Sides", "Snacks", "Drinks")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    Button(
                        onClick = { selectedCategory = category },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFFFF8C00) else Color.White,
                            contentColor = if (isSelected) Color.White else Color.Gray
                        ),
                        border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
                    ) {
                        Text(category)
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(foodItems) { item ->
                    // --- UPDATED CARD LOGIC ---
                    // Checking if the current item is in the user's favorites set
                    val isFavorited = favoritedItemNames.contains(item.name)
                    FoodItemCard(
                        menuItem = item,
                        isFavorited = isFavorited,
                        onFavoriteClick = { 
                            // Toggling logic: If it's already liked, remove it. Otherwise, add it!
                            if (isFavorited) {
                                favoritedItemNames = favoritedItemNames - item.name
                            } else {
                                favoritedItemNames = favoritedItemNames + item.name
                            }
                        },
                        onClick = { onItemClick(item) } 
                    )
                }
            }
        }
    }
}

/**
 * Reusable Card for each individual food item.
 * Updated: The heart icon now reacts to the 'isFavorited' state.
 */
@Composable
fun FoodItemCard(
    menuItem: MenuItem,
    isFavorited: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit 
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), 
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = menuItem.imageModel,
                    contentDescription = menuItem.name,
                    contentScale = ContentScale.Crop, 
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    error = rememberVectorPainter(Icons.Default.BrokenImage)
                )
                Spacer(Modifier.height(12.dp))
                Text(menuItem.name, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, maxLines = 1)
                Spacer(Modifier.height(4.dp))
                Text(menuItem.price, color = Color.Gray, fontSize = 14.sp)
            }

            // --- INTERACTIVE HEART ICON ---
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                // If 'isFavorited' is true, show filled orange heart.
                // If false, show the orange outline.
                Icon(
                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorited) "Remove from Favorites" else "Add to Favorites",
                    tint = Color(0xFFFF8C00)
                )
            }
        }
    }
}
