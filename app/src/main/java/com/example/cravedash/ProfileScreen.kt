package com.example.cravedash

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onAdminClick: () -> Unit = {}
) {
    val isDark   = ThemeManager.isDarkMode
    val bg       = MaterialTheme.colorScheme.background
    val surface  = MaterialTheme.colorScheme.surface
    val onBg     = MaterialTheme.colorScheme.onBackground
    val subtext  = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val cardBg   = if (isDark) Color(0xFF252525) else Color.White
    val divider  = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)

    var showBiometricDialog  by remember { mutableStateOf(false) }
    var biometricEnabled     by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Edit mode state
    var editMode     by remember { mutableStateOf(false) }
    var editFirst    by remember { mutableStateOf(UserSession.firstName) }
    var editLast     by remember { mutableStateOf(UserSession.lastName) }
    var editEmail    by remember { mutableStateOf(UserSession.email) }

    // Profile picture picker
    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { UserSession.profileImageUri = it.toString() } }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope             = rememberCoroutineScope()
    fun comingSoon(feature: String = "This feature") {
        scope.launch {
            snackbarHostState.showSnackbar("$feature is coming soon! 🚀")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Account", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData    = data,
                    containerColor  = if (isDark) Color(0xFF2A2A2A) else Color(0xFF1A1A1A),
                    contentColor    = Color.White,
                    actionColor     = Color(0xFFFF8C00),
                    shape           = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = bg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── PROFILE HERO CARD ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF8C00))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // ── Avatar + camera overlay ───────────────────────────────
                    Box(modifier = Modifier.size(96.dp)) {
                        // Profile photo or initials
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White.copy(0.25f))
                                .clickable { photoLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (UserSession.profileImageUri != null) {
                                AsyncImage(
                                    model              = UserSession.profileImageUri,
                                    contentDescription = "Profile photo",
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    UserSession.initials,
                                    color      = Color.White,
                                    fontSize   = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        // Camera badge (bottom-right)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { photoLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change photo",
                                tint     = Color(0xFFFF8C00),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (!editMode) {
                        // ── View mode ─────────────────────────────────────────
                        Text(
                            "${UserSession.firstName} ${UserSession.lastName}".trim().ifBlank { "Guest" },
                            color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold
                        )
                        if (UserSession.email.isNotBlank()) {
                            Text(UserSession.email, color = Color.White.copy(0.8f), fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                color = Color.White.copy(0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    "⭐  CraveDash Member",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
                                )
                            }
                            Surface(
                                color    = Color.White.copy(0.2f),
                                shape    = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { editMode = true }
                            ) {
                                Row(
                                    modifier          = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Edit, null,
                                        tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit", color = Color.White, fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    } else {
                        // ── Edit mode ─────────────────────────────────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value         = editFirst,
                                onValueChange = { editFirst = it },
                                label         = { Text("First name", color = Color.White.copy(0.7f)) },
                                modifier      = Modifier.weight(1f),
                                singleLine    = true,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = Color.White,
                                    unfocusedBorderColor = Color.White.copy(0.6f),
                                    focusedTextColor     = Color.White,
                                    unfocusedTextColor   = Color.White,
                                    cursorColor          = Color.White
                                )
                            )
                            OutlinedTextField(
                                value         = editLast,
                                onValueChange = { editLast = it },
                                label         = { Text("Last name", color = Color.White.copy(0.7f)) },
                                modifier      = Modifier.weight(1f),
                                singleLine    = true,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = Color.White,
                                    unfocusedBorderColor = Color.White.copy(0.6f),
                                    focusedTextColor     = Color.White,
                                    unfocusedTextColor   = Color.White,
                                    cursorColor          = Color.White
                                )
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value         = editEmail,
                            onValueChange = { editEmail = it },
                            label         = { Text("Email", color = Color.White.copy(0.7f)) },
                            modifier      = Modifier.fillMaxWidth(),
                            singleLine    = true,
                            colors        = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Color.White,
                                unfocusedBorderColor = Color.White.copy(0.6f),
                                focusedTextColor     = Color.White,
                                unfocusedTextColor   = Color.White,
                                cursorColor          = Color.White
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Save
                            Button(
                                onClick = {
                                    UserSession.firstName = editFirst.trim().ifBlank { "User" }
                                    UserSession.lastName  = editLast.trim()
                                    UserSession.email     = editEmail.trim()
                                    editMode = false
                                    scope.launch { snackbarHostState.showSnackbar("Profile saved ✓") }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor   = Color(0xFFFF8C00)
                                ),
                                shape     = RoundedCornerShape(10.dp),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text("Save", fontWeight = FontWeight.ExtraBold) }
                            // Cancel
                            OutlinedButton(
                                onClick = {
                                    editFirst = UserSession.firstName
                                    editLast  = UserSession.lastName
                                    editEmail = UserSession.email
                                    editMode  = false
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.6f)),
                                shape  = RoundedCornerShape(10.dp)
                            ) { Text("Cancel") }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── ACCOUNT ACTIVITY ─────────────────────────────────────────────
            ProfileSection("Account Activity", onBg) {
                ProfileRow(Icons.Default.History,      "My Orders",         subtext, cardBg, divider, onClick = onOrdersClick)
                ProfileRow(Icons.Default.LocationOn,   "Saved Addresses",   subtext, cardBg, divider, onClick = onAddressesClick)
                ProfileRow(Icons.Default.CreditCard,   "Payment Methods",   subtext, cardBg, divider, onClick = { comingSoon("Payment Methods") })
                ProfileRow(Icons.Default.CardGiftcard, "Promo Codes",       subtext, cardBg, divider, isLast = true, onClick = { comingSoon("Promo Codes") })
            }

            Spacer(Modifier.height(16.dp))

            // ── APPEARANCE ────────────────────────────────────────────────────
            ProfileSection("Appearance", onBg) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color    = cardBg
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Label row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) Color(0xFF3A3A3A) else Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    when (ThemeManager.theme) {
                                        AppTheme.DARK   -> Icons.Default.DarkMode
                                        AppTheme.LIGHT  -> Icons.Default.LightMode
                                        AppTheme.SYSTEM -> Icons.Default.SettingsBrightness
                                    },
                                    contentDescription = null,
                                    tint     = Color(0xFFFF8C00),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Display Theme",
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = onBg)
                                Text(
                                    when (ThemeManager.theme) {
                                        AppTheme.LIGHT  -> "Light mode"
                                        AppTheme.DARK   -> "Dark mode"
                                        AppTheme.SYSTEM -> "Follows system"
                                    },
                                    fontSize = 12.sp,
                                    color    = subtext
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        // 3-option selector
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F0F0))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                AppTheme.LIGHT  to "☀️  Light",
                                AppTheme.DARK   to "🌙  Dark",
                                AppTheme.SYSTEM to "📱  System"
                            ).forEach { (option, label) ->
                                val selected = ThemeManager.theme == option
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(
                                            if (selected) Color(0xFFFF8C00)
                                            else Color.Transparent
                                        )
                                        .clickable { ThemeManager.theme = option }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontSize   = 12.sp,
                                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
                                        color      = if (selected) Color.White else subtext
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(color = divider, thickness = 0.5.dp)
            }

            Spacer(Modifier.height(16.dp))

            // ── NOTIFICATIONS & SECURITY ──────────────────────────────────────
            ProfileSection("Settings", onBg) {
                // Notifications toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = cardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsIcon(Icons.Default.Notifications, isDark)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Push Notifications", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = onBg)
                            Text("Order updates & promotions", fontSize = 12.sp, color = subtext)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = Color(0xFFFF8C00),
                                checkedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCCCCCC),
                                uncheckedThumbColor = Color.White
                            )
                        )
                    }
                }
                HorizontalDivider(color = divider, thickness = 0.5.dp)

                // Biometrics toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = cardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SettingsIcon(Icons.Default.Fingerprint, isDark)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biometric Login", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = onBg)
                            Text("Use fingerprint to sign in", fontSize = 12.sp, color = subtext)
                        }
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { biometricEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = Color(0xFFFF8C00),
                                checkedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFCCCCCC),
                                uncheckedThumbColor = Color.White
                            )
                        )
                    }
                }
                HorizontalDivider(color = divider, thickness = 0.5.dp)

                ProfileRow(Icons.Default.Lock,         "Privacy & Security",subtext, cardBg, divider, onClick = { comingSoon("Privacy & Security") })
                ProfileRow(Icons.Default.Help,         "Help Center",       subtext, cardBg, divider, onClick = { comingSoon("Help Center") })
                ProfileRow(Icons.Default.Info,         "About CraveDash",   subtext, cardBg, divider, onClick = { comingSoon("About CraveDash") })
                ProfileRow(Icons.Default.AdminPanelSettings, "Admin — Menu Manager", subtext, cardBg, divider, isLast = true, onClick = { onAdminClick() })
            }

            Spacer(Modifier.height(20.dp))

            // ── LOGOUT ────────────────────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable(onClick = onLogoutClick),
                color = if (isDark) Color(0xFF2A1515) else Color(0xFFFEEBEB),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFE53935))
                    Spacer(Modifier.width(10.dp))
                    Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileSection(title: String, onBg: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp,
            color = Color(0xFFFF8C00), modifier = Modifier.padding(bottom = 8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            shadowElevation = if (ThemeManager.isDarkMode) 0.dp else 2.dp
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsIcon(icon: ImageVector, isDark: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF3A3A3A) else Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFFF8C00), modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ProfileRow(
    icon: ImageVector,
    label: String,
    subtext: Color,
    cardBg: Color,
    divider: Color,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        color = cardBg
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            SettingsIcon(icon, ThemeManager.isDarkMode)
            Spacer(Modifier.width(14.dp))
            Text(label, modifier = Modifier.weight(1f), fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = subtext)
        }
    }
    if (!isLast) Divider(color = divider, thickness = 0.5.dp)
}

// Legacy compat
@Composable
fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    ProfileRow(icon, label, Color.Gray, Color.White, Color(0xFFEEEEEE), onClick = onClick)
}
