package com.example.cravedash

import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Payment method tabs ───────────────────────────────────────────────────────
private enum class PayTab { CARD, BANK_TRANSFER, USSD }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onBackClick: () -> Unit, onPlaceOrderClick: () -> Unit) {
    val context = LocalContext.current
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val card    = if (isDark) Color(0xFF252525) else Color.White
    val divider = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)

    // Form state
    var selectedTab   by remember { mutableStateOf(PayTab.CARD) }
    var cardNumber    by remember { mutableStateOf("") }
    var cardName      by remember { mutableStateOf("") }
    var expiry        by remember { mutableStateOf("") }
    var cvv           by remember { mutableStateOf("") }
    var selectedBank  by remember { mutableStateOf("") }
    var selectedUssd  by remember { mutableStateOf("") }
    var isProcessing  by remember { mutableStateOf(false) }
    var showSuccess   by remember { mutableStateOf(false) }

    val ussdBanks = listOf(
        "GTBank" to "*737#", "Access Bank" to "*901#", "Zenith Bank" to "*966#",
        "First Bank" to "*894#", "UBA" to "*919#", "Sterling Bank" to "*822#"
    )

    // Format card number with spaces every 4 digits
    fun formatCard(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(16)
        return digits.chunked(4).joinToString(" ")
    }

    // Format expiry as MM/YY
    fun formatExpiry(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(4)
        return if (digits.length >= 3) "${digits.substring(0,2)}/${digits.substring(2)}" else digits
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = surface) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    // Total row
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = onBg)
                        Text("₦2,800", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFFFF8C00))
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            when (selectedTab) {
                                PayTab.CARD, PayTab.BANK_TRANSFER -> {
                                    // Opens Paystack hosted checkout inside the app (no SDK needed)
                                    // Replace pk_test_xxxx with your Paystack public key
                                    val paystackKey = "pk_test_xxxxxxxxxxxxxxxxxxxx"
                                    val ref = "cravedash_${System.currentTimeMillis()}"
                                    val email = Uri.encode(UserSession.email.ifBlank { "guest@cravedash.com" })
                                    val url = "https://paystack.com/pay/$ref?key=$paystackKey&amount=280000&email=$email"
                                    val colorScheme = CustomTabColorSchemeParams.Builder()
                                        .setToolbarColor(Color(0xFFFF8C00).toArgb()).build()
                                    CustomTabsIntent.Builder()
                                        .setDefaultColorSchemeParams(colorScheme)
                                        .setShowTitle(true)
                                        .build()
                                        .launchUrl(context, Uri.parse(url))
                                    // After returning from Paystack, navigate to success
                                    onPlaceOrderClick()
                                }
                                PayTab.USSD -> onPlaceOrderClick()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Pay Securely · ₦2,800", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = sub, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Secured by Paystack", fontSize = 11.sp, color = sub)
                    }
                }
            }
        },
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // ── ORDER SUMMARY ────────────────────────────────────────────────
            Surface(color = surface, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Order Summary", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = onBg)
                    Spacer(Modifier.height(12.dp))
                    SummaryRow("Basket Total", "₦2,300", onBg, sub)
                    SummaryRow("Delivery Fee", "₦500", onBg, sub)
                    Divider(color = divider, modifier = Modifier.padding(vertical = 8.dp))
                    SummaryRow("Total to Pay", "₦2,800", onBg, sub, isTotal = true)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── DELIVERY ADDRESS ─────────────────────────────────────────────
            Surface(color = surface, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF3E0)),
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, null, tint = Color(0xFFFF8C00))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Delivery Address", fontWeight = FontWeight.Bold, color = onBg)
                        Text("123 Crave Street, Lagos · +234 800 123 4567", color = sub, fontSize = 13.sp)
                    }
                    Text("Change", color = Color(0xFFFF8C00), fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { })
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── PAYMENT METHOD TABS ──────────────────────────────────────────
            Surface(color = surface, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Payment Method", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = onBg)
                    Spacer(Modifier.height(14.dp))

                    // Tab selector
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(
                        if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
                    )) {
                        listOf(
                            PayTab.CARD          to "💳 Card",
                            PayTab.BANK_TRANSFER to "🏦 Transfer",
                            PayTab.USSD          to "📱 USSD"
                        ).forEach { (tab, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedTab == tab) Color(0xFFFF8C00) else Color.Transparent)
                                    .clickable { selectedTab = tab }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTab == tab) Color.White else sub)
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── CARD FORM ────────────────────────────────────────────
                    AnimatedVisibility(visible = selectedTab == PayTab.CARD) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Card number with formatting
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = formatCard(it) },
                                label = { Text("Card Number") },
                                leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = sub) },
                                placeholder = { Text("0000 0000 0000 0000", color = sub) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = checkoutFieldColors(isDark, onBg, sub, divider)
                            )
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Cardholder Name") },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = sub) },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = checkoutFieldColors(isDark, onBg, sub, divider)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = expiry,
                                    onValueChange = { expiry = formatExpiry(it) },
                                    label = { Text("Expiry") },
                                    placeholder = { Text("MM/YY", color = sub) },
                                    modifier = Modifier.weight(1f), singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = checkoutFieldColors(isDark, onBg, sub, divider)
                                )
                                OutlinedTextField(
                                    value = cvv,
                                    onValueChange = { if (it.length <= 3) cvv = it },
                                    label = { Text("CVV") },
                                    modifier = Modifier.weight(1f), singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = checkoutFieldColors(isDark, onBg, sub, divider)
                                )
                            }
                            // Saved cards hint
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = sub, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Your card details are encrypted by Paystack", fontSize = 11.sp, color = sub)
                            }
                        }
                    }

                    // ── BANK TRANSFER ─────────────────────────────────────────
                    AnimatedVisibility(visible = selectedTab == PayTab.BANK_TRANSFER) {
                        Column {
                            Surface(
                                color = if (isDark) Color(0xFF1A3A1A) else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Transfer to this account:", fontWeight = FontWeight.Bold, color = onBg)
                                    Spacer(Modifier.height(10.dp))
                                    BankDetailRow("Bank",       "Wema Bank",          onBg, sub)
                                    BankDetailRow("Account No.","0123456789",         onBg, sub)
                                    BankDetailRow("Account",    "CraveDash Nigeria",  onBg, sub)
                                    BankDetailRow("Amount",     "₦2,800",             onBg, sub)
                                    Spacer(Modifier.height(6.dp))
                                    Text("⚡ Auto-confirmed in 60 seconds after transfer",
                                        fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    // ── USSD ──────────────────────────────────────────────────
                    AnimatedVisibility(visible = selectedTab == PayTab.USSD) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Select your bank and dial the code", fontSize = 13.sp, color = sub)
                            ussdBanks.forEach { (bankName, code) ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (selectedUssd == bankName) 2.dp else 1.dp,
                                            color = if (selectedUssd == bankName) Color(0xFFFF8C00) else divider,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedUssd = bankName },
                                    color = if (selectedUssd == bankName)
                                        (if (isDark) Color(0xFF3D2000) else Color(0xFFFFF3E0))
                                    else card,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🏦", fontSize = 20.sp)
                                            Spacer(Modifier.width(10.dp))
                                            Text(bankName, fontWeight = FontWeight.SemiBold, color = onBg)
                                        }
                                        Surface(color = Color(0xFFFF8C00), shape = RoundedCornerShape(8.dp)) {
                                            Text(code, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, onBg: Color, sub: Color, isTotal: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), Arrangement.SpaceBetween) {
        Text(label, color = if (isTotal) onBg else sub, fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = if (isTotal) Color(0xFFFF8C00) else onBg, fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Normal)
    }
}

@Composable
private fun BankDetailRow(label: String, value: String, onBg: Color, sub: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), Arrangement.SpaceBetween) {
        Text(label, color = sub, fontSize = 13.sp)
        Text(value, color = onBg, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
private fun checkoutFieldColors(isDark: Boolean, onBg: Color, sub: Color, divider: Color) =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor      = Color(0xFFFF8C00),
        unfocusedBorderColor    = divider,
        focusedTextColor        = onBg,
        unfocusedTextColor      = onBg,
        focusedLabelColor       = Color(0xFFFF8C00),
        unfocusedLabelColor     = sub,
        cursorColor             = Color(0xFFFF8C00),
        focusedContainerColor   = if (isDark) Color(0xFF1E1E1E) else Color.White,
        unfocusedContainerColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    )
