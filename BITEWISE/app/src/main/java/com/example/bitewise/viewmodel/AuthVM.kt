package com.example.bitewise.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class AuthVM : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    var loggedInEmail by mutableStateOf<String?>(null)
        private set

    var authError by mutableStateOf<String?>(null)
        private set

    // Helper to get the current user's UID
    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        // Initialize loggedInEmail if a user is already signed in
        auth.currentUser?.let {
            loggedInEmail = it.email
        }
    }

    fun register(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.length < 4) {
            authError = "Please enter valid email and password (min 4 characters)"
            onResult(false)
            return
        }
        authError = null // Clear previous error

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loggedInEmail = email
                    // authError = null // Already cleared
                    onResult(true)
                } else {
                    authError = task.exception?.message ?: "Registration failed"
                    onResult(false)
                }
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            authError = "Please enter both email and password."
            onResult(false)
            return
        }
        authError = null // Clear previous error

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loggedInEmail = email
                    // authError = null // Already cleared
                    onResult(true)
                } else {
                    authError = task.exception?.message ?: "Login failed"
                    onResult(false)
                }
            }
    }

    // Modified to include a callback for after logout actions
    fun logout(onLoggedOut: () -> Unit) {
        auth.signOut()
        loggedInEmail = null
        authError = null // Clear any auth errors on logout
        onLoggedOut()
    }
}