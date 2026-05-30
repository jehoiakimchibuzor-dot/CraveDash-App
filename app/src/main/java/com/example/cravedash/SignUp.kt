package com.example.cravedash

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

// ─────────────────────────────────────────────────────────────────────────────
// SIGN UP SCREEN — Glovo-style clean white design
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    var firstName       by remember { mutableStateOf("") }
    var lastName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPw       by remember { mutableStateOf("") }
    var pwVisible       by remember { mutableStateOf(false) }
    var cpwVisible      by remember { mutableStateOf(false) }
    var agreed          by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf("") }
    var isGoogleLoading by remember { mutableStateOf(false) }

    val pwStrength = checkPasswordStrength(password)
    val pwsMatch   = confirmPw.isEmpty() || password == confirmPw

    val strengthAnim by animateFloatAsState(
        targetValue   = pwStrength.fraction,
        animationSpec = tween(400),
        label         = "pwStrength"
    )

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
                    onSignUpClick()
                } catch (e: ApiException) {
                    if (e.statusCode != 12501) {
                        errorMsg = when (e.statusCode) {
                            7     -> "No internet. Check your connection and try again."
                            10    -> "App config error. Please contact support."
                            12502 -> "Sign-in in progress. Please wait."
                            else  -> "Google sign-up failed. Please try again."
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

            // ── TOP BAR: back + step indicator ───────────────────────────────
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                // Step indicator pill
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "New account",
                        color = Color(0xFFFF8C00),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── HEADLINE ─────────────────────────────────────────────────────
            Text(
                "Create account",
                color = Color(0xFF1A1A1A),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Join CraveDash and start ordering 🍛",
                color = Color(0xFF888888),
                fontSize = 15.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── GOOGLE SIGN-UP (prominent, at top like Glovo) ─────────────────
            GoogleSignInButton(
                label = "Sign up with Google",
                isLoading = isGoogleLoading,
                enabled = !isGoogleLoading,
                onClick = {
                    isGoogleLoading = true
                    errorMsg = ""
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
            AuthDivider(label = "or sign up with email")
            Spacer(Modifier.height(24.dp))

            // ── NAME ROW ─────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("First name")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text("John", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = cleanFieldColors()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Last name")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text("Doe", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = cleanFieldColors()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── EMAIL ─────────────────────────────────────────────────────────
            FieldLabel("Email address")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("you@example.com", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = cleanFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            // ── PHONE ─────────────────────────────────────────────────────────
            FieldLabel("Mobile number")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("08012345678", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = {
                    // Nigerian flag + country code prefix
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 14.dp, end = 4.dp)
                    ) {
                        Text("🇳🇬", fontSize = 16.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "+234",
                            color = Color(0xFF1A1A1A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(14.dp),
                colors = cleanFieldColors()
            )

            Spacer(Modifier.height(16.dp))

            // ── PASSWORD ──────────────────────────────────────────────────────
            FieldLabel("Password")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Min 8 characters", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(20.dp)) },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(
                            if (pwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
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

            // ── PASSWORD STRENGTH BAR ─────────────────────────────────────────
            if (password.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFEEEEEE))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(strengthAnim)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(2.dp))
                                .background(pwStrength.color)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            pwStrength.label,
                            color = pwStrength.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Use A–Z, 0–9, symbols",
                            color = Color(0xFFBBBBBB),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── CONFIRM PASSWORD ──────────────────────────────────────────────
            FieldLabel("Confirm password")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPw,
                onValueChange = { confirmPw = it },
                placeholder = { Text("Re-enter password", color = Color(0xFFCCCCCC), fontSize = 15.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(20.dp)) },
                visualTransformation = if (cpwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    if (confirmPw.isNotEmpty()) {
                        Icon(
                            if (pwsMatch) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            null,
                            tint = if (pwsMatch) Color(0xFF43A047) else Color(0xFFE53935),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        IconButton(onClick = { cpwVisible = !cpwVisible }) {
                            Icon(
                                if (cpwVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                isError = confirmPw.isNotEmpty() && !pwsMatch,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = cleanFieldColors()
            )
            if (confirmPw.isNotEmpty() && !pwsMatch) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Passwords don't match",
                    color = Color(0xFFE53935),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── TERMS CHECKBOX ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (agreed) Color(0xFFFFF8F0) else Color(0xFFFAFAFA))
                    .clickable { agreed = !agreed }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor   = Color(0xFFFF8C00),
                        uncheckedColor = Color(0xFFCCCCCC),
                        checkmarkColor = Color.White
                    ),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                val annotated = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF555555), fontSize = 13.sp)) {
                        append("I agree to CraveDash's ")
                    }
                    pushStringAnnotation("Privacy", "Privacy")
                    withStyle(SpanStyle(
                        color = Color(0xFFFF8C00),
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )) { append("Privacy Policy") }
                    pop()
                    withStyle(SpanStyle(color = Color(0xFF555555), fontSize = 13.sp)) { append(" and ") }
                    pushStringAnnotation("Terms", "Terms")
                    withStyle(SpanStyle(
                        color = Color(0xFFFF8C00),
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )) { append("Terms & Conditions") }
                    pop()
                }
                ClickableText(
                    text = annotated,
                    style = TextStyle(lineHeight = 20.sp),
                    modifier = Modifier.padding(top = 2.dp),
                    onClick = { offset ->
                        annotated.getStringAnnotations("Privacy", offset, offset)
                            .firstOrNull()?.let { onPrivacyClick() }
                        annotated.getStringAnnotations("Terms", offset, offset)
                            .firstOrNull()?.let { onTermsClick() }
                    }
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

            Spacer(Modifier.height(24.dp))

            // ── CREATE ACCOUNT BUTTON ─────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        firstName.isBlank() -> errorMsg = "Please enter your first name"
                        email.isBlank()     -> errorMsg = "Please enter your email address"
                        !email.contains("@") -> errorMsg = "Please enter a valid email address"
                        password.length < 6  -> errorMsg = "Password must be at least 6 characters"
                        !pwsMatch            -> errorMsg = "Passwords do not match"
                        !agreed              -> errorMsg = "Please accept the Terms & Conditions to continue"
                        else -> {
                            UserSession.firstName = firstName.trim()
                            UserSession.lastName  = lastName.trim()
                            UserSession.email     = email.trim()
                            onSignUpClick()
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
                enabled = !isGoogleLoading,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
            ) {
                Text(
                    "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── SIGN IN LINK ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color(0xFF888888), fontSize = 14.sp)
                Text(
                    "Sign In",
                    color = Color(0xFFFF8C00),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onBackClick() }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── LEGAL FOOTER ───────────────────────────────────────────────────
            Text(
                "Your data is safe with us. We never share your personal information with third parties.",
                color = Color(0xFFBBBBBB),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ── Reusable helper kept here for the file to compile standalone ──────────────
@Composable
fun ConsentItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFFF8C00),
                uncheckedColor = Color(0xFFCCCCCC)
            )
        )
        Text(
            text,
            color = Color(0xFF555555),
            fontSize = 13.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
