package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Address(val label: String, val fullAddress: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressesScreen(onBackClick: () -> Unit) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val card    = if (isDark) Color(0xFF252525) else Color.White
    val iconBg  = if (isDark) Color(0xFF333333) else Color(0xFFF0F0F0)

    val addresses = listOf(
        Address("Home",   "12 Adeniran Ogunsanya St, Surulere, Lagos",        Icons.Default.Home),
        Address("Office", "Civic Towers, Ozumba Mbadiwe Ave, Victoria Island", Icons.Default.Work),
        Address("Other",  "24 Cathedral Lane, Abuja",                          Icons.Default.LocationOn)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Addresses", fontWeight = FontWeight.Bold, color = onBg) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFFFF8C00),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, "Add Address") }
        },
        containerColor = bg
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(addresses) { addr ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = card),
                    elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 2.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(iconBg, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center) {
                            Icon(addr.icon, null, tint = Color(0xFFFF8C00))
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = onBg)
                            Text(addr.fullAddress, fontSize = 13.sp, color = sub)
                        }
                    }
                }
            }
        }
    }
}
