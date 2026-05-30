package com.example.cravedash

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

// ─────────────────────────────────────────────────────────────────────────────
// Password strength — shared across Login + SignUp
// ─────────────────────────────────────────────────────────────────────────────
data class PwStrength(val fraction: Float, val color: Color, val label: String)

fun checkPasswordStrength(pw: String): PwStrength {
    if (pw.isEmpty()) return PwStrength(0f, Color.Transparent, "")
    var score = 0
    if (pw.length >= 6)                                             score++
    if (pw.length >= 10)                                            score++
    if (pw.any { it.isUpperCase() } && pw.any { it.isLowerCase() }) score++
    if (pw.any { it.isDigit() })                                    score++
    if (pw.any { "!@#\$%^&*()_+-=[]{}|;:'\",.<>?".contains(it) })  score++
    return when {
        score <= 1 -> PwStrength(0.25f, Color(0xFFE53935), "Weak")
        score <= 3 -> PwStrength(0.60f, Color(0xFFFFB300), "Good")
        else       -> PwStrength(1.00f, Color(0xFF43A047), "Strong ✓")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOGIN SCREEN  —  Glovo-style clean white design
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLogInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context         = LocalContext.current
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf("") }

    // Google Sign-In
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestProfile().build()
    }
    val googleClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isGoogleLoading = false
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        .getResult(ApiException::class.java)
                    UserSession.firstName = account?.givenName  ?: "User"
                    UserSession.lastName  = account?.familyName ?: ""
                    UserSession.email     = account?.email      ?: ""
                    onLogInClick()
                } catch (e: ApiException) {
                    if (e.statusCode != 12501) {
                        errorMsg = when (e.statusCode) {
                            7     -> "No internet. Check your connection and try again."
                            10    -> "App config error. Please contact support."
                            12502 -> "Sign-in in progress. Please wait."
                            else  -> "Google sign-in failed. Please try again."
                        }
                    }
                }
            }
        }
    }

    // ── ROOT ─────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {

            // ── BACK BUTTON ──────────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── BRAND ────────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFF8C00)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CD",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "CraveDash",
                        color = Color(0xFF1A1A1A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Food delivered fast 🚀",
                        color = Color(0xFF888888),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── HEADLINE ─────────────────────────────────────────────────────
            Text(
                "Welcome back 👋",
                color = Color(0xFF1A1A1A),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Sign in to continue ordering",
                color = Color(0xFF888888),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(36.dp))

            // ── EMAIL FIELD ───────────────────────────────────────────────────
            FieldLabel("Email address")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMsg = "" },
                placeholder = { Text("you@example.com", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Email, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(20.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = cleanFieldColors()
            )

            Spacer(Modifier.height(20.dp))

            // ── PASSWORD FIELD ────────────────────────────────────────────────
            FieldLabel("Password")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                placeholder = { Text("Enter your password", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(20.dp))
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null,
                            tint = Color(0xFFAAAAAA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = cleanFieldColors()
            )

            // ── FORGOT PASSWORD ────────────────────────────────────────────────
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text(
                    "Forgot password?",
                    color = Color(0xFFFF8C00),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable { }
                )
            }

            // ── ERROR BANNER ───────────────────────────────────────────────────
            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF0F0))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Icon(Icons.Default.Error, null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(errorMsg, color = Color(0xFFE53935), fontSize = 13.sp, lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── SIGN IN BUTTON ────────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        email.isBlank()    -> errorMsg = "Please enter your email address"
                        password.isBlank() -> errorMsg = "Please enter your password"
                        else -> {
                            isLoading = true
                            if (UserSession.firstName == "Guest")
                                UserSession.firstName = email.substringBefore("@")
                                    .replaceFirstChar { it.uppercase() }
                            UserSession.email = email
                            onLogInClick()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF8C00),
                    disabledContainerColor = Color(0xFFFFCC99)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && !isGoogleLoading,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── DIVIDER ────────────────────────────────────────────────────────
            AuthDivider(label = "or continue with")

            Spacer(Modifier.height(24.dp))

            // ── GOOGLE BUTTON ──────────────────────────────────────────────────
            GoogleSignInButton(
                label = "Continue with Google",
                isLoading = isGoogleLoading,
                enabled = !isGoogleLoading && !isLoading,
                onClick = {
                    isGoogleLoading = true
                    errorMsg = ""
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                }
            )

            Spacer(Modifier.height(44.dp))

            // ── SIGN UP LINK ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = Color(0xFF888888), fontSize = 14.sp)
                Text(
                    "Sign Up",
                    color = Color(0xFFFF8C00),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── LEGAL FOOTER ───────────────────────────────────────────────────
            Text(
                "By signing in, you agree to our Terms of Service and Privacy Policy",
                color = Color(0xFFBBBBBB),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared small components — used by both LoginScreen and SignUpScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        color = Color(0xFF1A1A1A),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun AuthDivider(label: String = "or") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
        Text(
            "  $label  ",
            color = Color(0xFFAAAAAA),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color(0xFFEEEEEE))
    }
}

@Composable
fun GoogleSignInButton(
    label: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color(0xFFE8E8E8)),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF4285F4),
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(12.dp))
            Text("Please wait…", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF888888))
        } else {
            Image(
                painter = painterResource(R.drawable.ic_google),
                contentDescription = "Google",
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Field color tokens — Glovo-inspired light theme
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun cleanFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor        = Color(0xFFFF8C00),
    unfocusedBorderColor      = Color(0xFFE8E8E8),
    focusedTextColor          = Color(0xFF1A1A1A),
    unfocusedTextColor        = Color(0xFF1A1A1A),
    focusedLabelColor         = Color(0xFFFF8C00),
    unfocusedLabelColor       = Color(0xFF999999),
    cursorColor               = Color(0xFFFF8C00),
    focusedContainerColor     = Color.White,
    unfocusedContainerColor   = Color(0xFFF8F8F8),
    focusedLeadingIconColor   = Color(0xFFFF8C00),
    unfocusedLeadingIconColor = Color(0xFFAAAAAA),
    errorBorderColor          = Color(0xFFE53935),
    errorLabelColor           = Color(0xFFE53935),
    errorTextColor            = Color(0xFF1A1A1A),
    errorLeadingIconColor     = Color(0xFFE53935)
)
