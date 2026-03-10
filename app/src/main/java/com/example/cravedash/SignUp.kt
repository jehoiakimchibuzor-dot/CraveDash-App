package com.example.cravedash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * =================================================================================
 * SIGN UP SCREEN: A professional and secure registration form.
 * This screen collects user details while ensuring data privacy with masked passwords
 * and mandatory legal consents.
 * =================================================================================
 */
@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit, // Navigate to Dashboard after successful sign-up
    onPrivacyClick: () -> Unit, // Open the Privacy Policy screen
    onTermsClick: () -> Unit    // Open the Terms & Conditions screen
) {
    // --- STATE MANAGEMENT: Form Fields ---
    // We use 'remember' and 'mutableStateOf' to store the text typed by the user.
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // --- STATE MANAGEMENT: Privacy Toggles ---
    // These booleans track whether the user has clicked the "Eye" icon to reveal their password.
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // --- STATE MANAGEMENT: Checkboxes ---
    // Tracks user consent for various legal and marketing agreements.
    var promoConsent by remember { mutableStateOf(false) }
    var dataProcessingConsent by remember { mutableStateOf(false) }
    var privacyAgreement by remember { mutableStateOf(false) }

    // Using a Box to stack the background image and the form on top of each other.
    Box(modifier = Modifier.fillMaxSize()) {
        
        // --- 1. BACKGROUND IMAGE ---
        // Sets a professional atmospheric tone for the registration process.
        AsyncImage(
            model = "https://images.unsplash.com/photo-1543353071-873f17a7a088?w=800",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- 2. DARK OVERLAY ---
        // Adds a semi-transparent black layer to ensure the white text and fields are easy to see.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        // --- 3. THE REGISTRATION FORM ---
        // 'verticalScroll' allows users to swipe up/down if their screen is too small for all the fields.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            // Header text
            Text(
                text = "Join the Club",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Create your CraveDash account today",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.LightGray
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // --- FIRST & LAST NAME (ROW) ---
            // Side-by-side inputs for a compact and professional look.
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name", color = Color.White) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF8C00),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name", color = Color.White) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF8C00),
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- EMAIL ADDRESS INPUT ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // --- MOBILE NUMBER WITH COUNTRY CODE ---
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Mobile Number", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                // Country code prefix for Nigeria (+234)
                prefix = { Text("+234 ", color = Color.White, fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PASSWORD (MASKED) ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Create Password", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                // 'PasswordVisualTransformation' masks characters into dots for privacy.
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- CONFIRM PASSWORD (MASKED) ---
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF8C00),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONSENT SECTION ---
            // 1. Marketing Consent
            ConsentItem(
                checked = promoConsent,
                onCheckedChange = { promoConsent = it },
                text = "I would like to receive announcements and promotions from CraveDash"
            )

            // 2. NFDC Data Processing Consent
            ConsentItem(
                checked = dataProcessingConsent,
                onCheckedChange = { dataProcessingConsent = it },
                text = "I consent to the use of my personal information for processing for the Customer Relationship Management of the Nigerian Food Development Corporation. I acknowledge my data privacy rights including the option to withdraw my consent at anytime."
            )

            // 3. Privacy & Terms (Clickable links)
            val privacyAnnotatedString = buildAnnotatedString {
                append("I have fully read, understand, and agree to the ")
                pushStringAnnotation(tag = "Privacy", annotation = "Privacy")
                withStyle(style = SpanStyle(color = Color(0xFFFF8C00), textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                    append("Privacy policy")
                }
                pop()
                append(" and ")
                pushStringAnnotation(tag = "Terms", annotation = "Terms")
                withStyle(style = SpanStyle(color = Color(0xFFFF8C00), textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                    append("Terms & Conditions")
                }
                pop()
                append(", of the NFDC/CraveDash Nigeria")
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = privacyAgreement,
                    onCheckedChange = { privacyAgreement = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF8C00), uncheckedColor = Color.White)
                )
                // ClickableText detects which part of the text was clicked using the tags we pushed above.
                ClickableText(
                    text = privacyAnnotatedString,
                    modifier = Modifier.padding(start = 8.dp),
                    style = TextStyle(color = Color.LightGray, fontSize = 14.sp, lineHeight = 20.sp),
                    onClick = { offset ->
                        privacyAnnotatedString.getStringAnnotations(tag = "Privacy", start = offset, end = offset)
                            .firstOrNull()?.let { onPrivacyClick() }
                        privacyAnnotatedString.getStringAnnotations(tag = "Terms", start = offset, end = offset)
                            .firstOrNull()?.let { onTermsClick() }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // THE FINAL ACTION BUTTON
            Button(
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * REUSABLE COMPONENT: A checkbox paired with a text description.
 */
@Composable
fun ConsentItem(checked: Boolean, onCheckedChange: (Boolean) -> Unit, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF8C00), uncheckedColor = Color.White)
        )
        Text(
            text = text,
            color = Color.LightGray,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(start = 8.dp).clickable { onCheckedChange(!checked) }
        )
    }
}
