package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // magic tool for web photos

/**
 * =================================================================================
 * HOME SCREEN: This is where users choose to Log In or Sign Up.
 * Updated: Replaced the old icon with a high-quality food image for a premium feel.
 * =================================================================================
 */

// Add new actions for Terms and Privacy Policy
enum class HomeAction {
    SignUp,
    LogIn,
    GuestSignIn,
    TermsAndConditions,
    PrivacyPolicy
}

@Composable
fun HomeScreen(onAction: (HomeAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Title text
        Text(
            text = "Hungry for the best?",
            textAlign = TextAlign.Center,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color =  Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- THE PREMIUM IMAGE ---
        // Replacing the generic icon with a real photo to give it that "Geek/Pro" vibe
        AsyncImage(
            model = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=800",
            contentDescription = "Premium Food",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(24.dp))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Subtitle text
        Text(
            text = "Enjoy the convenience of having a CraveDash account!",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // --- LOGIN BUTTON ---
        Button(
            onClick = { onAction(HomeAction.LogIn) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF8C00)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Log in",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Sign up nudge
        Text(
            text = "Don't have an account yet? Sign up today!",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.DarkGray
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // --- SIGN UP BUTTON ---
        Button(
            onClick = { onAction(HomeAction.SignUp) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Sign up with email",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // --- LEGAL TEXT ---
        val annotatedString = buildAnnotatedString {
            append("By continuing, you agree to our updated ")
            pushStringAnnotation(tag = "Terms", annotation = "Terms")
            withStyle(style = SpanStyle(color = Color.Red, textDecoration = TextDecoration.Underline)) {
                append("Terms & Conditions")
            }
            pop()
            append(" and ")
            pushStringAnnotation(tag = "Privacy", annotation = "Privacy")
            withStyle(style = SpanStyle(color = Color.Red, textDecoration = TextDecoration.Underline)) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "Terms", start = offset, end = offset)
                    .firstOrNull()?.let { onAction(HomeAction.TermsAndConditions) }
                annotatedString.getStringAnnotations(tag = "Privacy", start = offset, end = offset)
                    .firstOrNull()?.let { onAction(HomeAction.PrivacyPolicy) }
            },
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Guest sign in link
        Text(
            text = "Sign in as a guest",
            modifier = Modifier.clickable { onAction(HomeAction.GuestSignIn) },
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Red
        )
        
        Spacer(modifier = Modifier.weight(1f))
    }
}
