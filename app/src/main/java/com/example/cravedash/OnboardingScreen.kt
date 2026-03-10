package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // For loading high-quality images

/**
 * =================================================================================
 * ONBOARDING SCREEN: The first impression.
 * I've removed the generic icon and added a high-quality "Hero" image for a pro look.
 * =================================================================================
 */
@Composable
fun OnboardingScreen(onGetStartedClick: () -> Unit){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Dark mode background for that "Geek" feel
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- THE HERO IMAGE ---
        // Replacing the spoon icon with a professional 3D-feeling food shot
        AsyncImage(
            model = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800",
            contentDescription = "Hero Food Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(32.dp)) // Soft rounded corners for modern look
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Grouping text for better visual hierarchy
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "CRAVE DASH",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Delivery app!",
                textAlign = TextAlign.Center,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFF8C00) // Our signature Orange
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Premium meals from the best chefs, delivered directly to your doorstep.",
                modifier = Modifier.fillMaxWidth(0.8f),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // THE GET STARTED BUTTON
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Get Started",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
