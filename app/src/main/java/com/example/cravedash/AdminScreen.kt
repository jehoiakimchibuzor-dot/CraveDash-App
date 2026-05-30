package com.example.cravedash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

// ─────────────────────────────────────────────────────────────────────────────
// Admin Screen — password-gated menu management panel
// Default password: cravedash  (change this before going live)
// ─────────────────────────────────────────────────────────────────────────────

private const val ADMIN_PASSWORD = "cravedash"

@Composable
fun AdminScreen(onBackClick: () -> Unit) {
    var authenticated by remember { mutableStateOf(false) }

    if (!authenticated) {
        AdminPasswordGate(
            onBackClick    = onBackClick,
            onAuthenticated = { authenticated = true }
        )
    } else {
        AdminDashboard(onBackClick = onBackClick)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Password gate
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminPasswordGate(onBackClick: () -> Unit, onAuthenticated: () -> Unit) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF888888)

    var password   by remember { mutableStateOf("") }
    var showPw     by remember { mutableStateOf(false) }
    var wrongPw    by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Access", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        containerColor = bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AdminPanelSettings, null,
                    tint = Color(0xFFFF8C00), modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Menu Manager",
                fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = onBg)
            Spacer(Modifier.height(6.dp))
            Text("Enter your admin password to continue",
                fontSize = 14.sp, color = sub, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value         = password,
                onValueChange = { password = it; wrongPw = false },
                label         = { Text("Admin Password") },
                leadingIcon   = { Icon(Icons.Default.Lock, null) },
                visualTransformation = if (showPw) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                trailingIcon  = {
                    IconButton(onClick = { showPw = !showPw }) {
                        Icon(if (showPw) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                isError  = wrongPw,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape    = RoundedCornerShape(14.dp),
                colors   = cleanFieldColors()
            )
            if (wrongPw) {
                Spacer(Modifier.height(6.dp))
                Text("Incorrect password. Try again.",
                    color = Color(0xFFE53935), fontSize = 12.sp)
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (password == ADMIN_PASSWORD) onAuthenticated()
                    else wrongPw = true
                },
                modifier  = Modifier.fillMaxWidth().height(52.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape     = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Enter Admin Panel", fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Main admin dashboard
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminDashboard(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF888888)
    val card    = if (isDark) Color(0xFF252525) else Color.White

    var selectedCategory by remember { mutableStateOf("All") }
    var showAddEdit      by remember { mutableStateOf(false) }
    var editingItem      by remember { mutableStateOf<FoodItem?>(null) }
    var deleteTarget     by remember { mutableStateOf<FoodItem?>(null) }
    var showResetDialog  by remember { mutableStateOf(false) }

    val categories = listOf("All") + MenuRepository.categories
    val displayItems by remember(selectedCategory) {
        derivedStateOf {
            if (selectedCategory == "All") MenuRepository.items.toList()
            else MenuRepository.items.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Menu Manager", fontWeight = FontWeight.Bold, color = onBg)
                        Text("${MenuRepository.items.size} items  ·  ${categories.drop(1).joinToString(" · ") { cat ->
                            "${MenuRepository.byCategory(cat).size} ${cat}"
                        }}", fontSize = 11.sp, color = sub)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg)
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(Icons.Default.RestartAlt, null, tint = sub)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { editingItem = null; showAddEdit = true },
                containerColor = Color(0xFFFF8C00),
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Default.Add, "Add item")
            }
        },
        containerColor = bg
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Category filter tabs ─────────────────────────────────────────
            Surface(color = surface, shadowElevation = 2.dp) {
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val sel = cat == selectedCategory
                        Surface(
                            onClick = { selectedCategory = cat },
                            shape   = RoundedCornerShape(20.dp),
                            color   = if (sel) Color(0xFFFF8C00) else Color.Transparent,
                            border  = if (!sel) androidx.compose.foundation.BorderStroke(
                                1.dp, if (isDark) Color(0xFF444444) else Color(0xFFEEEEEE)
                            ) else null
                        ) {
                            Text(
                                "$cat${if (cat != "All") " (${MenuRepository.byCategory(cat).size})" else " (${MenuRepository.items.size})"}",
                                modifier   = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                color      = if (sel) Color.White else sub,
                                fontSize   = 13.sp,
                                fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // ── Item list ────────────────────────────────────────────────────
            if (displayItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🍽️", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No items in $selectedCategory",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = onBg)
                        Spacer(Modifier.height(8.dp))
                        Text("Tap + to add a new item",
                            fontSize = 13.sp, color = sub)
                    }
                }
            } else {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayItems, key = { it.name + it.category }) { item ->
                        AdminItemRow(
                            item     = item,
                            card     = card,
                            onBg     = onBg,
                            sub      = sub,
                            isDark   = isDark,
                            onEdit   = { editingItem = item; showAddEdit = true },
                            onDelete = { deleteTarget = item }
                        )
                    }
                    // Space for FAB
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }

    // ── Add / Edit dialog ────────────────────────────────────────────────────
    if (showAddEdit) {
        AddEditItemDialog(
            existing  = editingItem,
            onDismiss = { showAddEdit = false; editingItem = null },
            onSave    = { newItem ->
                if (editingItem != null) {
                    MenuRepository.update(editingItem!!, newItem, context)
                } else {
                    MenuRepository.add(newItem, context)
                }
                showAddEdit = false
                editingItem = null
            }
        )
    }

    // ── Delete confirmation ──────────────────────────────────────────────────
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            icon    = { Icon(Icons.Default.Delete, null, tint = Color(0xFFE53935)) },
            title   = { Text("Delete item?", fontWeight = FontWeight.Bold) },
            text    = { Text("\"${target.name}\" will be permanently removed from the menu.") },
            confirmButton = {
                TextButton(onClick = {
                    MenuRepository.delete(target, context)
                    deleteTarget = null
                }) {
                    Text("Delete", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ── Reset to defaults confirmation ───────────────────────────────────────
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon    = { Icon(Icons.Default.RestartAlt, null, tint = Color(0xFFFF8C00)) },
            title   = { Text("Reset to defaults?", fontWeight = FontWeight.Bold) },
            text    = { Text("All your custom changes will be lost and the original menu will be restored.") },
            confirmButton = {
                TextButton(onClick = {
                    MenuRepository.resetToDefaults(context)
                    showResetDialog = false
                }) {
                    Text("Reset", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Item row card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AdminItemRow(
    item: FoodItem,
    card: Color, onBg: Color, sub: Color, isDark: Boolean,
    onEdit: () -> Unit, onDelete: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = card),
        elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model              = item.imageUrl.ifBlank { null },
                contentDescription = item.name,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF2A2A2A) else Color(0xFFEEEEEE))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = onBg,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(item.price,
                        color = Color(0xFFFF8C00),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 13.sp)
                    Surface(
                        color = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(item.category,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            fontSize = 10.sp, color = sub)
                    }
                }
            }
            // Edit / Delete buttons
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFFF8C00), modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = sub, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add / Edit form dialog
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditItemDialog(
    existing: FoodItem?,
    onDismiss: () -> Unit,
    onSave: (FoodItem) -> Unit
) {
    val isDark = ThemeManager.isDarkMode
    val onBg   = MaterialTheme.colorScheme.onBackground

    var name     by remember { mutableStateOf(existing?.name        ?: "") }
    var price    by remember { mutableStateOf(existing?.price?.removePrefix("₦") ?: "") }
    var category by remember { mutableStateOf(existing?.category    ?: "Meals") }
    var desc     by remember { mutableStateOf(existing?.description ?: "") }
    var imageUrl by remember { mutableStateOf(existing?.imageUrl    ?: "") }
    var showPreview by remember { mutableStateOf(existing?.imageUrl?.isNotBlank() == true) }
    var error    by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape           = RoundedCornerShape(20.dp),
            color           = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier        = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header
                Text(
                    if (existing != null) "Edit Item" else "Add New Item",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = onBg
                )
                Spacer(Modifier.height(20.dp))

                // Name
                OutlinedTextField(
                    value         = name,
                    onValueChange = { name = it; error = "" },
                    label         = { Text("Item Name *") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    isError       = error.isNotEmpty() && name.isBlank(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = cleanFieldColors()
                )
                Spacer(Modifier.height(12.dp))

                // Price
                OutlinedTextField(
                    value         = price,
                    onValueChange = { price = it.filter { c -> c.isDigit() || c == ',' }; error = "" },
                    label         = { Text("Price (₦) *") },
                    prefix        = { Text("₦", color = Color(0xFFFF8C00), fontWeight = FontWeight.Bold) },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError       = error.isNotEmpty() && price.isBlank(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = cleanFieldColors()
                )
                Spacer(Modifier.height(12.dp))

                // Category selector
                Text("Category", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = onBg)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    listOf("Meals", "Grills", "Sides", "Desserts", "Drinks").forEach { cat ->
                        val sel = cat == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (sel) Color(0xFFFF8C00) else Color.Transparent)
                                .clickable { category = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat,
                                fontSize   = 12.sp,
                                fontWeight = if (sel) FontWeight.ExtraBold else FontWeight.Normal,
                                color      = if (sel) Color.White
                                else if (isDark) Color(0xFF888888)
                                else Color(0xFF666666))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Description
                OutlinedTextField(
                    value         = desc,
                    onValueChange = { desc = it },
                    label         = { Text("Description") },
                    modifier      = Modifier.fillMaxWidth(),
                    maxLines      = 3,
                    shape         = RoundedCornerShape(12.dp),
                    colors        = cleanFieldColors()
                )
                Spacer(Modifier.height(12.dp))

                // Image URL + preview
                OutlinedTextField(
                    value         = imageUrl,
                    onValueChange = { imageUrl = it; showPreview = false },
                    label         = { Text("Image URL (optional)") },
                    leadingIcon   = { Icon(Icons.Default.Image, null) },
                    trailingIcon  = {
                        if (imageUrl.isNotBlank()) {
                            IconButton(onClick = { showPreview = true }) {
                                Icon(Icons.Default.Visibility, "Preview",
                                    tint = Color(0xFFFF8C00))
                            }
                        }
                    },
                    modifier    = Modifier.fillMaxWidth(),
                    singleLine  = true,
                    shape       = RoundedCornerShape(12.dp),
                    colors      = cleanFieldColors()
                )

                // Image preview
                AnimatedVisibility(visible = showPreview && imageUrl.isNotBlank()) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        AsyncImage(
                            model              = imageUrl,
                            contentDescription = "Preview",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF2A2A2A) else Color(0xFFEEEEEE))
                        )
                    }
                }

                // Error
                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = Color(0xFFE53935), fontSize = 12.sp)
                }

                Spacer(Modifier.height(24.dp))

                // Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick   = onDismiss,
                        modifier  = Modifier.weight(1f).height(48.dp),
                        shape     = RoundedCornerShape(12.dp)
                    ) { Text("Cancel") }

                    Button(
                        onClick = {
                            when {
                                name.isBlank()  -> error = "Item name is required"
                                price.isBlank() -> error = "Price is required"
                                else -> onSave(
                                    FoodItem(
                                        name        = name.trim(),
                                        price       = "₦${price.replace(",", "").trim()}",
                                        category    = category,
                                        description = desc.trim(),
                                        imageUrl    = imageUrl.trim()
                                    )
                                )
                            }
                        },
                        modifier  = Modifier.weight(1f).height(48.dp),
                        colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                        shape     = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            if (existing != null) "Update" else "Add Item",
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color.White
                        )
                    }
                }
            }
        }
    }
}
