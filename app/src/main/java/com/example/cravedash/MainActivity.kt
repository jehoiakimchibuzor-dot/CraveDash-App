package com.example.cravedash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cravedash.ui.theme.CraveDashTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import coil.compose.AsyncImage

/**
 * 🚀 MAIN ACTIVITY: The high-level orchestrator of the app.
 * Updated: Added Success and Addresses screens to the navigation map.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CraveDashTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "onboarding") {

                        // --- ONBOARDING & HOME ---
                        composable("onboarding") { OnboardingScreen { navController.navigate("home") } }
                        composable("home") {
                            HomeScreen(onAction = { action ->
                                when (action) {
                                    HomeAction.SignUp -> navController.navigate("signup")
                                    HomeAction.LogIn -> navController.navigate("login")
                                    HomeAction.GuestSignIn -> navController.navigate("DashBoard") { popUpTo("home") { inclusive = true } }
                                    HomeAction.TermsAndConditions -> navController.navigate("terms")
                                    HomeAction.PrivacyPolicy -> navController.navigate("privacy")
                                }
                            })
                        }

                        // --- AUTHENTICATION ---
                        composable("login") { LoginScreen { navController.navigate("DashBoard") { popUpTo("home") { inclusive = true } } } }
                        composable("signup") {
                            SignUpScreen(
                                onSignUpClick = { navController.navigate("DashBoard") { popUpTo("home") { inclusive = true } } },
                                onPrivacyClick = { navController.navigate("privacy") },
                                onTermsClick = { navController.navigate("terms") }
                            ) 
                        }

                        // --- DASHBOARD ---
                        composable("DashBoard") {
                            DashBoard(
                                onMealClick = { navController.navigate("menu/Meals") },
                                onSideClick = { navController.navigate("menu/Sides") },
                                onSnackClick = { navController.navigate("menu/Snacks") },
                                onDrinkClick = { navController.navigate("menu/Drinks") },
                                onChatClick = { navController.navigate("chat") },
                                onItemClick = { item ->
                                    val name = URLEncoder.encode(item.name, StandardCharsets.UTF_8.toString())
                                    val price = URLEncoder.encode(item.price, StandardCharsets.UTF_8.toString())
                                    val desc = URLEncoder.encode(item.description, StandardCharsets.UTF_8.toString())
                                    val img = URLEncoder.encode(item.imageUrl, StandardCharsets.UTF_8.toString())
                                    navController.navigate("foodDetails/$name/$price/$desc/$img")
                                }
                            )
                        }

                        // --- MENU & DETAILS ---
                        composable("menu/{category}", arguments = listOf(navArgument("category") { type = NavType.StringType })) { entry ->
                            val cat = entry.arguments?.getString("category") ?: "Meals"
                            MenuScreen(
                                initialCategory = cat, 
                                onBackClick = { navController.popBackStack() }, 
                                onCartClick = { navController.navigate("cart") }, 
                                onHomeClick = { navController.navigate("DashBoard") }, 
                                onMenuClick = {}, 
                                onProfileClick = { navController.navigate("profile") }, 
                                onFavoriteClick = { navController.navigate("favorites") }, 
                                onLiveChatClick = { navController.navigate("chat") }, 
                                onItemClick = { item ->
                                    val name = URLEncoder.encode(item.name, StandardCharsets.UTF_8.toString())
                                    val price = URLEncoder.encode(item.price, StandardCharsets.UTF_8.toString())
                                    val desc = URLEncoder.encode(item.description, StandardCharsets.UTF_8.toString())
                                    val img = URLEncoder.encode(item.imageModel.toString(), StandardCharsets.UTF_8.toString())
                                    navController.navigate("foodDetails/$name/$price/$desc/$img")
                                }
                            )
                        }

                        composable("foodDetails/{itemName}/{itemPrice}/{description}/{itemImage}", arguments = listOf(navArgument("itemName") { type = NavType.StringType }, navArgument("itemPrice") { type = NavType.StringType }, navArgument("description") { type = NavType.StringType }, navArgument("itemImage") { type = NavType.StringType })) { entry ->
                            FoodDetailsScreen(itemName = entry.arguments?.getString("itemName") ?: "", itemPrice = entry.arguments?.getString("itemPrice") ?: "", description = entry.arguments?.getString("description") ?: "", itemImage = entry.arguments?.getString("itemImage") ?: "", onBackClick = { navController.popBackStack() }, onCartClick = { navController.navigate("cart") })
                        }

                        // --- CART & CHECKOUT ---
                        composable("cart") { CartScreen(onBackClick = { navController.popBackStack() }, onCheckoutClick = { navController.navigate("checkout") }) }
                        
                        composable("checkout") { 
                            CheckoutScreen(
                                onBackClick = { navController.popBackStack() }, 
                                onPlaceOrderClick = { navController.navigate("orderSuccess") } // Connected!
                            ) 
                        }
                        
                        composable("orderSuccess") { 
                            OrderSuccessScreen(
                                onTrackOrderClick = { navController.navigate("orderTracking") }, 
                                onContinueShoppingClick = { navController.navigate("DashBoard") { popUpTo("DashBoard") { inclusive = true } } }
                            ) 
                        }

                        composable("orderTracking") {
                            OrderTrackingScreen(onBackClick = { navController.popBackStack() })
                        }

                        // --- PROFILE & SETTINGS ---
                        composable("favorites") {
                            FavoritesScreen(
                                onBackClick = { navController.popBackStack() },
                                onCartClick = { navController.navigate("cart") },
                                onItemClick = { item ->
                                    val name = URLEncoder.encode(item.name, StandardCharsets.UTF_8.toString())
                                    val price = URLEncoder.encode(item.price, StandardCharsets.UTF_8.toString())
                                    val desc = URLEncoder.encode(item.description, StandardCharsets.UTF_8.toString())
                                    val img = URLEncoder.encode(item.imageModel.toString(), StandardCharsets.UTF_8.toString())
                                    navController.navigate("foodDetails/$name/$price/$desc/$img")
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBackClick = { navController.popBackStack() },
                                onLogoutClick = { navController.navigate("home") { popUpTo(0) { inclusive = true } } },
                                onOrdersClick = { navController.navigate("orderHistory") },
                                onAddressesClick = { navController.navigate("savedAddresses") } // Connected!
                            )
                        }
                        composable("orderHistory") { OrderHistoryScreen(onBackClick = { navController.popBackStack() }) }
                        composable("savedAddresses") { SavedAddressesScreen(onBackClick = { navController.popBackStack() }) }

                        // --- LIVE CHAT ---
                        composable("chat") { LiveChatScreen(onBackClick = { navController.popBackStack() }) }

                        // --- LEGAL SCREENS ---
                        composable("terms") { 
                            LegalScreen(title = "Terms & Conditions", content = "Standard terms...", onAccept = { navController.popBackStack() }, onDecline = { navController.popBackStack() })
                        }
                        composable("privacy") { 
                            LegalScreen(title = "Privacy Policy", content = "Standard privacy...", onAccept = { navController.popBackStack() }, onDecline = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

/**
 * REUSABLE LEGAL SCREEN: Atmospheric dark version.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(title: String, content: String, onAccept: () -> Unit, onDecline: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(model = "https://images.unsplash.com/photo-1556910103-1c02745a309e?w=800", contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Black.copy(alpha = 0.9f)))))
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold, color = Color.White) }, navigationIcon = { IconButton(onClick = onDecline) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)) },
            bottomBar = {
                Surface(color = Color.Black.copy(alpha = 0.8f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = onDecline, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))) { Text("Decline", color = Color.White) }
                        Button(onClick = onAccept, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) { Text("I Accept", fontWeight = FontWeight.Bold) }
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp)) {
                Text(text = "Please review these documents to continue using CraveDash.", fontSize = 14.sp, color = Color.LightGray, modifier = Modifier.padding(bottom = 24.dp))
                Text(text = content, fontSize = 16.sp, color = Color.White, lineHeight = 26.sp)
                Spacer(modifier = Modifier.height(80.dp)) 
            }
        }
    }
}
