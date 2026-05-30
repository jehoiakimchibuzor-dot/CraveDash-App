package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.text.ClickableText
import coil.compose.AsyncImage

enum class HomeAction {
    SignUp, LogIn, GuestSignIn, TermsAndConditions, PrivacyPolicy
}

// ─────────────────────────────────────────────────────────────────────────────
// Home / Welcome Screen — premium food delivery landing page
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(onAction: (HomeAction) -> Unit) {

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Full-screen hero image ────────────────────────────────────────────
        AsyncImage(
            model              = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=900",
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        // Dark gradient overlay — heavier toward the bottom
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.00f to Color.Black.copy(0.25f),
                    0.42f to Color.Black.copy(0.50f),
                    0.68f to Color.Black.copy(0.75f),
                    1.00f to Color.Black.copy(0.96f)
                )
            )
        )

        // ── Top: CraveDash branding ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFFFF8C00)),
                contentAlignment = Alignment.Center
            ) {
                Text("CD", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("CraveDash",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold)
                Text("Lagos, Nigeria  📍",
                    color = Color.White.copy(0.65f),
                    fontSize = 11.sp)
            }
        }

        // ── Mid: Hero copy ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 280.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                color = Color(0xFFFF8C00).copy(0.22f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    "⚡  Fast delivery · 25–35 min",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    color = Color(0xFFFF8C00),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Hungry?\nWe've got you.",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 48.sp,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Premium Nigerian food, delivered in minutes.",
                color = Color.White.copy(0.72f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }

        // ── Bottom: Auth sheet (rounded white card) ───────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Handle indicator
            Box(
                modifier = Modifier
                    .size(40.dp, 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDDDDDD))
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Welcome to CraveDash",
                color = Color(0xFF1A1A1A),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Sign in or create an account to order",
                color = Color(0xFF888888),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(24.dp))

            // ── Log In ────────────────────────────────────────────────────────
            Button(
                onClick   = { onAction(HomeAction.LogIn) },
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape     = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Log In",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // ── Create Account ────────────────────────────────────────────────
            OutlinedButton(
                onClick  = { onAction(HomeAction.SignUp) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1A1A1A)
                )
            ) {
                Text("Create Account",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF1A1A1A))
            }

            Spacer(Modifier.height(20.dp))

            // ── Terms ─────────────────────────────────────────────────────────
            val terms = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFFAAAAAA), fontSize = 12.sp)) {
                    append("By continuing, you agree to our ")
                }
                pushStringAnnotation("Terms", "Terms")
                withStyle(SpanStyle(
                    color = Color(0xFFFF8C00),
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp
                )) { append("Terms") }
                pop()
                withStyle(SpanStyle(color = Color(0xFFAAAAAA), fontSize = 12.sp)) { append(" & ") }
                pushStringAnnotation("Privacy", "Privacy")
                withStyle(SpanStyle(
                    color = Color(0xFFFF8C00),
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp
                )) { append("Privacy Policy") }
                pop()
            }
            ClickableText(
                text  = terms,
                style = TextStyle(textAlign = TextAlign.Center),
                onClick = { offset ->
                    terms.getStringAnnotations("Terms",   offset, offset).firstOrNull()?.let { onAction(HomeAction.TermsAndConditions) }
                    terms.getStringAnnotations("Privacy", offset, offset).firstOrNull()?.let { onAction(HomeAction.PrivacyPolicy) }
                }
            )

            Spacer(Modifier.height(16.dp))

            // ── Guest ─────────────────────────────────────────────────────────
            Text(
                "Continue as guest  →",
                color    = Color(0xFFFF8C00),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onAction(HomeAction.GuestSignIn) }
            )
        }
    }
}
