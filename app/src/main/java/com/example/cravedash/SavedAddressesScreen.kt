package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * =================================================================================
 * SAVED ADDRESSES SCREEN: Managing the user's delivery locations.
 * Features: High contrast, "Sleek Geek" icons, and a bright "Add New" button.
 * =================================================================================
 */

data class Address(val label: String, val fullAddress: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressesScreen(onBackClick: () -> Unit) {
    val addresses = listOf(
        Address("Home", "12 Adeniran Ogunsanya St, Surulere, Lagos", Icons.Default.Home),
        Address("Office", "Civic Towers, Ozumba Mbadiwe Ave, Victoria Island", Icons.Default.Work),
        Address("Other", "24 Cathedral Lane, Abuja", Icons.Default.LocationOn)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Addresses", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add New */ },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) { Icon(Icons.Default.Add, contentDescription = "Add Address") }
        },
        containerColor = Color(0xFFF8F8F8)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            items(addresses) { address ->
                AddressCard(address)
            }
        }
    }
}

@Composable
fun AddressCard(address: Address) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(address.icon, contentDescription = null, tint = Color.Black) }
            
            Spacer(Modifier.width(16.dp))
            
            Column {
                Text(text = address.label, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = address.fullAddress, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
