package com.example.cravedash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global in-memory session.
 * Set on login/sign-up, read everywhere.
 * profileImageUri — holds the local URI string of the user's picked photo.
 */
object UserSession {
    var firstName       by mutableStateOf("Guest")
    var lastName        by mutableStateOf("")
    var email           by mutableStateOf("")
    var profileImageUri by mutableStateOf<String?>(null)   // null = show initials

    val displayName get() = if (firstName == "Guest") "Guest" else firstName

    val initials get() = buildString {
        firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
        lastName.firstOrNull()?.let  { append(it.uppercaseChar()) }
        if (isEmpty()) append("G")
    }
}
