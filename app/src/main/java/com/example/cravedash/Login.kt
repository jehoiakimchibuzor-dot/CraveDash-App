package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // magic tool for web photos

/**
 * =================================================================================
 * LOGIN SCREEN: Updated with a premium "Geek" aesthetic.
 * Now featuring a "Show/Hide Password" toggle and a dedicated Country Code for Phone Number.
 * =================================================================================
 */
@Composable
fun LoginScreen(onLogInClick: () -> Unit) {
    // Input state variables
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Privacy toggle state
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        
        // --- BACKGROUND ATMOSPHERE ---
        AsyncImage(
            model = "https://images.unsplash.com/photo-1551218808-94e220e084d2?w=800",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for contrast
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text = "Welcome Back", textAlign = TextAlign.Center, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(text = "Login to your CraveDash account", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.LightGray)
            
            Spacer(modifier = Modifier.height(48.dp))

            // --- PHONE NUMBER INPUT WITH COUNTRY CODE ---
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                // --- ADDED COUNTRY CODE PREFIX ---
                // This 'prefix' keeps the country code visible and uneditable
                prefix = { Text("+234 ", color = Color.White, fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF8C00)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- PASSWORD INPUT (MASKED) ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.White)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFFFF8C00)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogInClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
